#!/usr/bin/env python3
"""Generate an AI code review for the latest changes."""
from __future__ import annotations

import json
import os
import subprocess
import sys
from pathlib import Path
from typing import Tuple

MAX_DIFF_CHARS = int(os.getenv("AI_REVIEW_MAX_DIFF_CHARS", "12000"))
DEFAULT_MODEL = os.getenv("GEMINI_MODEL", "gemini-1.5-flash")


def run(cmd: list[str]) -> str:
    return subprocess.check_output(cmd, text=True, stderr=subprocess.STDOUT)


def load_event() -> dict:
    event_path = os.getenv("GITHUB_EVENT_PATH")
    if not event_path:
        return {}
    path = Path(event_path)
    if not path.exists():
        return {}
    return json.loads(path.read_text())


def resolve_range(event: dict, event_name: str) -> Tuple[str | None, str | None]:
    if event_name == "pull_request":
        pr = event.get("pull_request", {})
        return pr.get("base", {}).get("sha"), pr.get("head", {}).get("sha")
    if event_name == "push":
        return event.get("before"), event.get("after")
    return None, None


def load_ruleset() -> str:
    rules_path = Path(__file__).resolve().parents[1] / "docs" / "ai-review-ruleset.md"
    if rules_path.exists():
        return rules_path.read_text().strip()
    return "No custom ruleset provided."


def collect_diff(base: str | None, head: str | None) -> Tuple[str, bool, str, str]:
    """Return diff text (possibly truncated) and the resolved SHAs."""
    if not head:
        head = run(["git", "rev-parse", "HEAD"]).strip()
    if not base:
        base = run(["git", "rev-parse", f"{head}~1"]).strip()

    diff_cmd = ["git", "diff", f"{base}..{head}", "--unified=3", "--no-color"]
    diff = run(diff_cmd)

    truncated = False
    if len(diff) > MAX_DIFF_CHARS:
        diff = diff[:MAX_DIFF_CHARS]
        truncated = True

    return diff, truncated, base, head


def build_prompt(ruleset: str, diff: str, truncated: bool) -> str:
    note = "\n\nNote: Diff was truncated for size; limit conclusions accordingly." if truncated else ""
    return f"""Repository ruleset:
{ruleset}

Perform a concise senior-level code review of the diff below.
- Focus on correctness, security, data handling, performance, and missing tests.
- Group findings, avoid restating the diff, and keep to <= 8 bullets.
- If nothing critical is found, say so and mention any residual risk.
{note}

Diff:
{diff}
"""


def generate_review(prompt: str, model: str) -> str:
    try:
        import google.generativeai as genai
    except ImportError as exc:
        raise RuntimeError("google-generativeai package is required. Did pip install run?") from exc

    api_key = os.getenv("GEMINI_API_KEY")
    if not api_key:
        raise RuntimeError("GEMINI_API_KEY is not set in the environment.")

    genai.configure(api_key=api_key)
    
    system_instruction = "You are a precise, concise code reviewer. Call out critical issues and missing tests."
    full_prompt = f"{system_instruction}\n\n{prompt}"
    
    client = genai.GenerativeModel(
        model_name=model,
        generation_config={
            "temperature": 0.2,
            "max_output_tokens": 800,
        }
    )
    
    response = client.generate_content(full_prompt)
    return response.text.strip()


def write_report(content: str, base: str, head: str, event_name: str, model: str, truncated: bool) -> Path:
    report = Path("ai-review.md")
    header = [
        "# AI Commit Review",
        f"- Event: {event_name}",
        f"- Base: {base}",
        f"- Head: {head}",
        f"- Model: {model}",
        "",
        "## Findings",
        content,
    ]
    if truncated:
        header.append("\n_Note: Diff truncated for size; review only covers the visible changes._")
    report.write_text("\n".join(header).strip() + "\n")
    return report


def main() -> int:
    event = load_event()
    event_name = os.getenv("GITHUB_EVENT_NAME", "local")
    base, head = resolve_range(event, event_name)
    diff, truncated, base, head = collect_diff(base, head)

    ruleset = load_ruleset()
    prompt = build_prompt(ruleset, diff, truncated)
    review = generate_review(prompt, DEFAULT_MODEL)
    report_path = write_report(review, base, head, event_name, DEFAULT_MODEL, truncated)

    print(report_path.read_text())
    return 0


if __name__ == "__main__":
    try:
        sys.exit(main())
    except subprocess.CalledProcessError as err:
        print(f"Command failed: {err.cmd}\n{err.output}", file=sys.stderr)
        sys.exit(1)
    except Exception as exc:  # pylint: disable=broad-except
        print(f"Error: {exc}", file=sys.stderr)
        sys.exit(1)
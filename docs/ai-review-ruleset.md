# AI Review Ruleset

Use these guardrails when generating automated code reviews:

## Goals
- Catch correctness, security, data handling, and performance regressions.
- Highlight missing or insufficient tests for changed behavior.
- Point to specific files/functions when possible.

## What to Flag
- Silent error handling, missing input validation, and insecure defaults.
- Breaking API changes or incompatible data shape updates.
- Inefficient queries or obvious scalability bottlenecks introduced in the diff.
- Lack of coverage when critical logic changed (mention expected test areas).

## How to Respond
- Keep output concise (aim for <= 8 bullet points).
- Group related findings and avoid restating the diff.
- If the diff looks fine, state that explicitly and mention any residual risk (e.g., untested paths).
- If the diff was truncated, say so and limit conclusions to what was visible.
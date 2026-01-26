import os
import json
import google.generativeai as genai
from github import Github

def get_pr_details():
    """Reads PR details from GitHub Actions event payload."""
    with open(os.getenv('GITHUB_EVENT_PATH'), 'r') as f:
        event = json.load(f)
    return event['pull_request']

def get_diff(repo, pr_number):
    """Fetches the diff of the Pull Request."""
    pr = repo.get_pull(pr_number)
    # Using 'requests' or direct access might be needed if PyGithub doesn't give raw diff easily,
    # but pr.get_files() gives files. However, for a full diff suitable for LLM, 
    # we might want the patch for each file.
    diff_content = ""
    for file in pr.get_files():
        diff_content += f"file: {file.filename}\n"
        diff_content += f"status: {file.status}\n"
        diff_content += f"patch:\n{file.patch}\n\n"
    return diff_content

def main():
    api_key = os.getenv("GEMINI_API_KEY")
    github_token = os.getenv("GITHUB_TOKEN")

    if not api_key:
        print("Error: GEMINI_API_KEY is missing.")
        return
    if not github_token:
        print("Error: GITHUB_TOKEN is missing.")
        return

    # 1. Setup Gemini
    genai.configure(api_key=api_key)
    model = genai.GenerativeModel('gemini-2.5-flash')

    # 2. Setup GitHub
    g = Github(github_token)
    repo_name = os.getenv("GITHUB_REPOSITORY")
    repo = g.get_repo(repo_name)

    # 3. Get PR info
    try:
        pr_data = get_pr_details()
        pr_number = pr_data['number']
    except Exception as e:
        print(f"Failed to get PR details: {e}")
        return

    print(f"Reviewing PR #{pr_number} in {repo_name}")

    # 4. Get Diff
    diff_text = get_diff(repo, pr_number)
    if not diff_text:
        print("No diff found or empty PR.")
        return

    # 5. Get Ruleset
    ruleset = ""
    ruleset_path = "docs/ai-review-ruleset.md"
    if os.path.exists(ruleset_path):
        with open(ruleset_path, "r", encoding="utf-8") as f:
            ruleset = f.read()

    # 6. Generate Prompt
    prompt = f"""
You are an expert Senior Software Engineer and Code Reviewer. 
Your task is to review the following code changes (diff) from a GitHub Pull Request.

**Context:**
- Repository: {repo_name}

**Instructions:**
1. Analyze the usage of the changes.
2. Identify potential bugs, security issues, performance problems, or bad practices.
3. Suggest improvements or refactoring if necessary.
4. Be crucial but constructive and polite.
5. If the code is good, just say "LGTM!" and give a brief positive summary.
6. **IMPORTANT:** Follow the specific project rules below (if any).

**Project Rules:**
{ruleset}

**Code Changes (Diff):**
```diff
{diff_text}
```

**Output Format:**
Please provide your review in Markdown format.
"""

    # 7. Call Gemini
    try:
        response = model.generate_content(prompt)
        review_comment = response.text
    except Exception as e:
        print(f"Gemini API Error: {e}")
        return

    # 8. Post Comment
    pr = repo.get_pull(pr_number)
    pr.create_issue_comment(review_comment)
    print("Review comment posted successfully.")

if __name__ == "__main__":
    main()

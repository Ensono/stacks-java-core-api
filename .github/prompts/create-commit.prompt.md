---
agent: agent
name: create-commit
description: Create a clean, comprehensive conventional commit with GPG signing and pre-commit validation.
model: GPT-5 mini (copilot)
---

# Conventional Commit Creation Prompt

## Purpose

This prompt guides an AI agent to create clean, comprehensive, and conventional commits that adhere to project security guidelines. It includes GPG signing verification, pre-commit hook handling, and failure diagnosis.

## Prerequisites

- Git repository with staged or unstaged changes
- GPG signing configured and operational
- Pre-commit hooks installed (if applicable)

## User Input

```text
$ARGUMENTS
```

The user may provide:

- A description of the changes (e.g., "fixed the pipeline working directory")
- A commit type override (e.g., "feat:", "fix:", "chore:")
- Nothing (analyze changes to determine appropriate message)

---

## Phase 1: Environment Verification

### 1.1 Verify GPG Signing Configuration

**CRITICAL**: GPG signing is MANDATORY. Never bypass or disable it.

```bash
git config --get commit.gpgsign
git config --get user.signingkey
```

**Expected output**:

- `commit.gpgsign` = `true`
- `user.signingkey` = a valid key ID

**If GPG signing is not configured or fails**:

⚠️ **GPG SIGNING REQUIRED**

GPG signing is mandatory per project security policy. Do NOT proceed without it.

**Diagnostic commands**:

```bash
# List available GPG keys
gpg --list-secret-keys --keyid-format LONG

# Check GPG agent status
gpgconf --list-components

# Verify signing key matches commit email
git config user.email
git config user.signingkey
```

**Resolution steps**:

1. If no signing key configured: `git config user.signingkey <KEY_ID>`
2. If GPG agent not running: `gpgconf --launch gpg-agent`
3. If key expired: Generate new key or extend expiration

**STOP** and report the issue to the user. Do NOT attempt to bypass GPG signing.

### 1.2 Verify Git User Configuration

```bash
git config user.name
git config user.email
```

Ensure both are set before proceeding.

---

## Phase 2: Analyze Changes

### 2.1 Check Repository State

```bash
git status --porcelain
```

### 2.2 Categorize Changes

**If no changes exist**: Report to user and stop.

**If unstaged changes exist**:

```bash
git diff --stat
git diff  # Full diff for context
```

**If staged changes exist**:

```bash
git diff --cached --stat
git diff --cached  # Full diff for context
```

### 2.3 Determine Commit Scope

Analyze the changed files to determine:

| File Pattern                        | Likely Type | Scope Suggestion     |
| ----------------------------------- | ----------- | -------------------- |
| `src/main/**/*.java`                | feat/fix    | Component/class name |
| `src/test/**/*.java`                | test        | Test category        |
| `pom.xml`, `build.gradle`           | chore       | deps                 |
| `*.yml`, `*.yaml` (CI/CD)           | ci          | Pipeline name        |
| `README.md`, `docs/**`              | docs        | Documentation area   |
| `.github/**`, `build/**`            | ci/chore    | Tooling area         |
| `Dockerfile`, `docker-compose.yml`  | chore       | docker               |
| Configuration files                 | chore       | config               |
| Security-related changes            | security    | Affected component   |
| Refactoring without behavior change | refactor    | Affected component   |

---

## Phase 3: Construct Commit Message

### 3.1 Conventional Commit Format

```text
<type>(<scope>): <subject>

<body>

<footer>
```

### 3.2 Commit Types (Conventional Commits 1.0.0)

| Type       | Description                                           | SemVer Impact |
| ---------- | ----------------------------------------------------- | ------------- |
| `feat`     | New feature for the user                              | MINOR         |
| `fix`      | Bug fix for the user                                  | PATCH         |
| `docs`     | Documentation only changes                            | None          |
| `style`    | Formatting, missing semicolons, etc. (no code change) | None          |
| `refactor` | Code change that neither fixes a bug nor adds feature | None          |
| `perf`     | Performance improvement                               | PATCH         |
| `test`     | Adding or correcting tests                            | None          |
| `build`    | Changes to build system or dependencies               | None          |
| `ci`       | Changes to CI configuration files and scripts         | None          |
| `chore`    | Other changes that don't modify src or test files     | None          |
| `revert`   | Reverts a previous commit                             | Varies        |
| `security` | Security-related changes                              | PATCH/MINOR   |

### 3.3 Subject Line Rules

- **Maximum 50 characters** (hard limit: 72)
- Use **imperative mood** ("add" not "added" or "adds")
- **No period** at the end
- **Lowercase** first letter after type
- Be **specific** but **concise**

**Good examples**:

- `fix(pipeline): correct working directory for archetype build`
- `feat(auth): add JWT token refresh endpoint`
- `docs(readme): update installation instructions`

**Bad examples**:

- `Fixed bug` (too vague, wrong tense)
- `Updated the authentication controller to support new tokens.` (too long, has period)
- `WIP` (not descriptive)

### 3.4 Body Guidelines

- Wrap at **72 characters**
- Explain **what** and **why**, not how (code shows how)
- Use bullet points for multiple changes
- Reference relevant context (error messages, requirements)

### 3.5 Footer Guidelines

- Reference issues: `Fixes #123`, `Closes #456`, `Refs #789`
- Breaking changes: `BREAKING CHANGE: <description>`
- Co-authors: `Co-authored-by: Name <email>`

---

## Phase 4: Stage Changes (If Needed)

### 4.1 If Changes Are Unstaged

Prompt user for confirmation before staging:

```bash
# Stage specific files
git add <file1> <file2>

# Or stage all changes
git add -A
```

### 4.2 Verify Staged Content

```bash
git diff --cached --stat
```

Confirm with user that staged changes match intent.

---

## Phase 5: Execute Commit

### 5.1 Attempt Commit

```bash
git commit -m "<type>(<scope>): <subject>

<body>

<footer>"
```

**Note**: GPG signing happens automatically if `commit.gpgsign=true`.

### 5.2 Handle Pre-Commit Hook Results

**If pre-commit succeeds**: Report success and show commit hash.

**If pre-commit fails**: Proceed to Phase 6.

---

## Phase 6: Pre-Commit Failure Diagnosis & Resolution

### 6.1 Common Pre-Commit Hooks & Fixes

| Hook                   | Common Failures                   | Resolution                                |
| ---------------------- | --------------------------------- | ----------------------------------------- |
| `yamllint`             | YAML syntax/style violations      | Fix indentation, quotes, line length      |
| `eslint`               | JavaScript/TypeScript lint errors | Apply `--fix` or manual corrections       |
| `checkstyle`           | Java style violations             | Fix formatting per project rules          |
| `prettier`             | Formatting inconsistencies        | Run `prettier --write <files>`            |
| `black`                | Python formatting                 | Run `black <files>`                       |
| `trailing-whitespace`  | Whitespace at end of lines        | Remove trailing whitespace                |
| `end-of-file-fixer`    | Missing newline at EOF            | Add newline at end of file                |
| `check-merge-conflict` | Merge conflict markers            | Resolve conflict markers                  |
| `detect-secrets`       | Potential secrets in code         | Remove secrets, use env vars              |
| `gitleaks`             | Secrets/credentials detected      | Remove sensitive data from commit         |
| `commitlint`           | Commit message format violation   | Reformat message per conventional commits |

### 6.2 Diagnostic Process

1. **Capture the full error output** from the failed hook
2. **Identify which hook failed** from the output
3. **Read the specific error messages** to understand violations
4. **Apply targeted fixes** based on the hook type

### 6.3 Resolution Workflow

```bash
# 1. Check what hooks are configured
cat .pre-commit-config.yaml

# 2. Run pre-commit manually to see all issues
pre-commit run --all-files

# 3. Run specific hook for faster iteration
pre-commit run <hook-id> --files <affected-files>

# 4. After fixing, re-stage changes
git add -A

# 5. Retry commit
git commit -m "<message>"
```

### 6.4 Handling Specific Failures

#### YAML Lint Failures

```bash
# Check yamllint configuration
cat yamllint.conf  # or .yamllint.yml

# Run yamllint directly for detailed errors
yamllint -c yamllint.conf <file.yml>

# Common fixes:
# - Ensure consistent indentation (usually 2 spaces)
# - Quote strings containing special characters
# - Ensure document starts with ---
# - Check line length limits
```

#### Java Checkstyle Failures

```bash
# Run checkstyle via Maven
./mvnw checkstyle:check

# Review specific violations in output
# Apply fixes per project's checkstyle.xml rules
```

#### Secret Detection Failures

**CRITICAL**: If secrets are detected:

1. **Do NOT commit** the secret
2. **Remove the secret** from the file
3. **Use environment variables** or a secrets manager
4. If secret was previously committed, consider it **compromised**

### 6.5 After Fixing

1. **Re-stage the fixed files**:

   ```bash
   git add -A
   ```

2. **Verify fixes**:

   ```bash
   pre-commit run --all-files
   ```

3. **Retry the commit**:

   ```bash
   git commit -m "<message>"
   ```

4. **If still failing**: Report detailed error to user with analysis

---

## Phase 7: Post-Commit Verification

### 7.1 Verify Commit Created

```bash
git log -1 --show-signature
```

**Verify**:

- Commit message matches expected format
- GPG signature is valid (`Good signature`)
- Author information is correct

### 7.2 Report to User

Provide summary:

```text
✅ Commit created successfully

Commit: <hash>
Author: <name> <email>
GPG Signature: Valid (Key: <key-id>)

Message:
<type>(<scope>): <subject>

<body>

Files changed:
- <file1>
- <file2>

Next steps:
- Push with: git push origin HEAD
- Or continue working on additional changes
```

---

## Security Compliance Checklist

Before finalizing ANY commit, verify:

- [ ] GPG signing is enabled and working
- [ ] No secrets, credentials, or API keys in committed files
- [ ] No security controls disabled or bypassed
- [ ] Commit does not modify protected branch directly
- [ ] Pre-commit hooks have passed
- [ ] Commit message follows conventional format
- [ ] Changes are properly scoped (no unrelated modifications)

---

## Error Recovery

### GPG Signing Failed

```text
⚠️ GPG SIGNING FAILED

Do NOT attempt to bypass GPG signing. This is a security requirement.

Diagnostic steps:
1. gpg --list-secret-keys --keyid-format LONG
2. git config user.signingkey
3. echo "test" | gpg --clearsign

If GPG agent issues:
- gpgconf --kill gpg-agent
- gpgconf --launch gpg-agent
- export GPG_TTY=$(tty)
```

### Pre-Commit Continues to Fail

If pre-commit hooks fail repeatedly after multiple fix attempts:

1. **Document all attempted fixes**
2. **Report specific error messages** to user
3. **Suggest manual review** of hook configuration
4. **Do NOT bypass hooks** without explicit user approval and documented justification

### Commit Rejected by Remote

If push fails due to branch protection:

```text
🛡️ BRANCH PROTECTION ACTIVE

This branch has protection rules. You must:
1. Create a feature branch
2. Push changes to feature branch
3. Open a Pull Request
4. Obtain required approvals
5. Merge via PR process
```

---

## Example Workflow

**User input**: "fix the pipeline issue"

**Agent workflow**:

1. ✅ Verify GPG signing configured
2. ✅ Check `git status` - finds modified `azure-pipelines.yml`
3. ✅ Analyze diff - working directory path changed
4. ✅ Construct message:

   ```text
   fix(ci): correct working directory for Maven archetype build

   The ArchetypeBuild job was using self_repo_dir as the working
   directory, but mvnw is located in the java/ subdirectory. Changed
   to self_project_dir to match the actual location of the Maven
   wrapper script.
   ```

5. ✅ Stage changes: `git add azure-pipelines.yml`
6. ✅ Commit: `git commit -m "..."`
7. ✅ Pre-commit passes (yamllint, etc.)
8. ✅ GPG signature applied
9. ✅ Report success with commit hash

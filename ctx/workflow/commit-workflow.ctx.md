# Commit Workflow CTX

It is recommended to create this file at `ctx/workflow/commit-workflow.ctx.md`.
It is generated automatically when `init-project.sh` runs.

---

## 1. Commit Separation Criteria

Without this rule, the AI mixes domain changes and response changes into a single commit.

- Always separate changes to the query-result structure in the domain layer from changes to response exposure in the application layer into different commits.

## 2. Commit Ordering Rules

Without this rule, the AI reverses the dependency order.

- When a response-layer change depends on a domain query-result change, always commit the domain change first.
- Place documentation-cleanup commits after the related code-change commits.

## 3. File Inclusion Scope Rules

Without this rule, the AI mixes files and breaks per-commit tracking.

- Include only files of the same responsibility in a single commit.
- Compose each commit so it is reproducible with file-level `git add` alone.

## 4. Commit Message Rules

Without this rule, the AI omits or mixes up messages.

- Always follow the title format `type: (scope) summary`.
- The body structure and language rules are defined by the `ctx-commit-planner` Skill. Do not redefine them here.

## Forbidden patterns

- No WIP commits
- No empty commit messages
- No commits composed solely of auto-generated files

## Required pre-commit checks

- [ ] Build passes
- [ ] Tests pass

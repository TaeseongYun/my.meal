# mymeal

## Project Overview
(TODO: brief description of this project)

## Tech Stack
- (TODO)

## Development Rules
- (TODO: coding conventions, naming rules, etc.)

## Hallucination Guard (ALWAYS ON)
Verify every dev fact (paths, symbols, API sigs, config keys, versions) against a concrete
source before stating it — prefer graphify (`graphify query`/`graphify explain`/`graphify path`,
or the MCP tools) over memory; codegraph/grep/Read as fallback. Never verify a guess with another guess. Read
`aidlc-docs/hallucination-ledger.md` first; never reuse a quarantined claim.
Full rules: /Users/yoontaeseong/work/aidlc-workflow/extensions/hallucination-guard/hallucination-guard.md
Audit loop: /ctx-hallucination-audit (run until Hallucination-Free Score ≥ 87).

## Worktree Rules (synced 2026-08-27)
- 워크트리는 반드시 `git worktree add`(ctx-worktree 스킬의 allocator 경유)로 생성한다. mkdir 금지.
- 워크트리마다 자체 그래프를 빌드한다 (`graphify .` / `graphify . --update`). `graphify-out/`은 git-ignore.
- 브랜치를 main에 머지한 후에는 main의 그래프를 재생성한다 (`graphify . --update`).

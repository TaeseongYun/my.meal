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
- 워크트리마다 자체 그래프를 빌드한다 (`graphify .` / `graphify update .`). `graphify-out/`은 git-ignore.
- 브랜치를 main에 머지한 후에는 main의 그래프를 재생성한다 (`graphify update .`).

## Graphify (Kotlin AST, 2026-09-04)
- CLI 문법은 `graphify update .` (구 `graphify . --update`는 무효).
- graphify 0.17.1은 Kotlin 그램마를 번들하지 않는다. `tree-sitter-kotlin.wasm`(fwcd 릴리스 0.3.8, ABI 14)을
  `/opt/homebrew/lib/node_modules/@sentropic/graphify/node_modules/tree-sitter-kotlin/`에 배치해 해결했다.
  npm `tree-sitter-wasms` 사본은 ABI 불일치로 로드 실패 — **fwcd GitHub 릴리스 wasm만** 사용할 것.
  graphify 재설치 시 이 파일이 사라지므로 그래프가 비면 먼저 이 경로를 확인한다.
- 설명·라벨은 어시스턴트 2단계: `graphify update .` → `.graphify/description-instructions/batch-*.json`과
  `label-instructions/communities.json`을 채움 → 재실행해 반영. 언어는 `--description-lang ko`로 지정(자동 감지가 오작동).

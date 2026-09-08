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
- **주의**: 토폴로지가 바뀌는 재생성은 기존 노드 설명을 전부 날린다. `.graphify/<날짜>/` 백업이 남지만
  이미 비워진 상태가 떠질 수 있어 복원이 보장되지 않는다. 재생성 직후 커버리지
  (`graphify summary` 또는 graph.json의 description 개수)를 반드시 확인하고, 비었으면 배치를 다시 채운다.

## Review Tier & PR 규율 (2026-09-08)
- **머지는 사용자만 한다.** 세션은 `gh pr create`까지. `gh pr merge` 금지.
- PR 생성 전 티어를 분류해 본문 첫 줄에 `Tier: T0|T1|T2`를 적는다:
  ```
  git diff --name-only origin/main...HEAD \
   | grep -qE '^(gradle/libs\.versions\.toml|gradle\.properties|core/data/schemas/|supabase/migrations/|core/auth/|.*/(androidMain|iosMain)/|.*AndroidManifest\.xml)' \
   && echo T2 || echo "T1-or-T0"
  ```
  T2는 **사람의 골든패스 실행이 머지 조건**이다(에뮬레이터 실행 → 로그인 → 기록 저장 → 재실행 세션 복원).
  T0 = docs/ctx/테스트만. 나머지는 T1.
- feat PR은 **추가 ≤600줄 / 파일 ≤20개**. 초과하면 `/ctx-commit-planner`로 슬라이스를 나눈다.
- **`gradle/libs.versions.toml`의 버전 신설·상향은 단독 PR**로 낸다(그 버전을 쓰는 실경로 테스트와
  모듈 배선만 동반 허용). 신규 모듈의 `build.gradle.kts`/`settings.gradle.kts` 변경은 이 규칙 대상이 아니다.
  근거: PR #11에서 버전 7줄이 1000줄에 묻혀 런타임 크래시가 머지됐다.
- 외부 라이브러리를 새로 붙일 때는 **페이크가 아니라 실제 라이브러리 코드를 앱과 같은 클래스패스에서
  1회 실행하는 스모크 테스트**를 함께 둔다 (`core/auth/.../SupabaseWireSmokeTest.kt` 참고).
  모듈 단독 클래스패스는 앱과 버전 해석이 다를 수 있으니 카탈로그 버전을 테스트에 명시할 것.

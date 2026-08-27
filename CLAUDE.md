# mymeal

## Project Overview
(TODO: brief description of this project)

## Tech Stack
- (TODO)

## Development Rules
- (TODO: coding conventions, naming rules, etc.)

## Hallucination Guard (ALWAYS ON)
Verify every dev fact (paths, symbols, API sigs, config keys, versions) against a concrete
source before stating it — prefer codegraph (`codegraph explore`/`codegraph node`) over memory,
grep/Read as fallback. Never verify a guess with another guess. Read
`aidlc-docs/hallucination-ledger.md` first; never reuse a quarantined claim.
Full rules: /Users/yoontaeseong/workspace/aidlc-workflow/extensions/hallucination-guard/hallucination-guard.md
Audit loop: /ctx-hallucination-audit (run until Hallucination-Free Score ≥ 87).

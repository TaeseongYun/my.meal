# Status — design-system

- Feature: design-system
- Status: **implemented** (2026-08-27, 워크트리 design-system 브랜치)
- Request Type: prepared-requirement (피그마 스타일 가이드 = 준비된 입력물)
- Depth Level: standard
- Project State: brownfield (KMP 템플릿 — 데모 코드만 존재)
- Roadmap Context: standalone (단일 기능, `_roadmap.md` 없음. 팀/워크트리 분할 불필요)
- Last Updated: 2026-08-27

## Readiness Score: 99 / 110 (90%) — READY

| 영역 | 점수 | 만점 | 근거 |
|------|------|------|------|
| 1. 기능 범위 정의 | 15 | 15 | Goal/In/Out 명확, scope BLOCK 없음 |
| 2. 정책/예외 확정 | 20 | 20 | 토큰 값 매니페스트 확정 + Q1~Q5 전건 답변 (확신: 확실) |
| 3. 사용자 시나리오 | 9 | 15 | 소비자=개발자(토큰 참조 API). 화면 시나리오는 본 기능 범위 아님 |
| 4. NFR 확인 | 13 | 15 | 성능/보안/운영 해당 없음 명시. 렌더링 일관성만 테스트 대상 |
| 5. 승인 항목 해결 | 20 | 20 | BLOCK 0건, GATE-2 통과 (2026-08-27) |
| 6. 리스크 평가 | 12 | 15 | UNCERTAIN(폰트 lineHeight 렌더링) 잔존 → 80% 캡 적용 |
| 8. 시스템 구조 설계 (보너스) | 10 | 10 | GATE-2.7 트리거됨. 컴포넌트 식별/책임 4, API 경계(SikdorokTheme 단일 진입점) 3, 의존성 무순환 3 |

GATE-2.5(사용자 스토리): 미트리거 — 보너스 영역 7 제외. READY 기준: 110 x 80% = 88점, 현재 99점.

## Uncertain Areas

- ~~이서윤체 lineHeight 렌더링~~ 해소 (2026-08-27): StyleGuideScreen 쇼케이스를 Android 에뮬레이터(Medium_Phone)에서 실행 — 전 타이포 단계 잘림/겹침 없음, 국·영문 이서윤체 통일 확인. iOS/실기기 확인은 선택 사항
- ⚠️ 이서윤체 배포 라이선스 — 사용자가 파일 제공 시 함께 확인 (Q1=A)

## Open Dependencies

- ~~이서윤체 TTF 파일~~ 해소 (2026-08-27): 사용자 제공 → `core/designsystem/src/commonMain/composeResources/font/lee_seoyun_regular.ttf` 배치 완료

## Progress

- [x] STEP 1 / 1-B / 1-C / 1.5 / 2 / 3 / 4 / 5
- [x] GATE-2 통과 (2026-08-27, 답변: A/B/C/A/:core:designsystem)
- [-] STEP 5.5 / GATE-2.5 스킵 (사용자 시나리오 <3, 신규 사용자 유형 없음)
- [x] STEP 5.7 (application-design)
- [x] GATE-2.7 승인 (2026-08-27, DroidKaigi 구조 확인 후 톱레벨 :core:designsystem 확정)
- [x] STEP 6 (UOW 분해 — 4개: M/S/M/M)
- [x] GATE-3 승인 (2026-08-27, "B")
- [x] STEP 6.5 (technical-design.md — ADR 3건)
- [x] GATE-3.5 승인 (2026-08-27, "B" — ADR-3 매핑 표 확정)
- [-] STEP 6.7 / GATE-4 스킵 (인프라 변경 없음)
- [x] STEP 7 (Readiness 재확인: 99/110 유지 — UNCERTAIN 1건으로 영역 6 캡 지속)
- [x] STEP 8 (판정: READY — 구현 진행 가능)
- [x] STEP 9 (build-instructions.md, test-instructions.md)
- [x] GATE-5 승인 (2026-08-27, "B 처리하되 워크트리로 진행") → 구현은 전용 워크트리에서 /ctx-run
- [x] 구현 완료 (/ctx-run ROLE 0~6): UOW-1~4 전건, 테스트 6/6 green (Android host + iOS sim), 빌드 통과
- [x] 육안 확인 완료 (에뮬레이터, StyleGuideScreen 쇼케이스) / main 병합 완료

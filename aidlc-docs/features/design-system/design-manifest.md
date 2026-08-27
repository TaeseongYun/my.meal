# Design Manifest — Sikdorok Style Guide

Source of truth snapshot. Extracted via Figma REST API on 2026-08-27.

- File: `T3hswISeWAzU2nD6hESZJL` (💡 ideation)
- Node: `836:33127` "Style Guide" (FRAME, 900x2800)
- Brand: Sikdorok (식도록) — 도시락 기록 앱
- Raw API dump: scratchpad `node-full.json` (session-local; re-fetchable from the node above)

## Color

### Bg
| Semantic | Palette | Hex | Alpha |
|----------|---------|-----|-------|
| BG 1 | Gray 0 | #FFFFFF | 100% |
| BG 2 | Beige 1 | #FCFAF7 | 100% |
| BG 3 | Beige 2 | #F8F5ED | 100% |
| BG 3 (중복 라벨) | Beige 3 | #E9E6DE | 100% |
| Line | — | #9D9792 | 10% |

> ⚠️ 원본 디자인에서 "BG 3" 라벨이 #F8F5ED와 #E9E6DE 두 색에 중복 사용됨.

### Txt / Btn
| Semantic | Palette | Hex | Alpha |
|----------|---------|-----|-------|
| Text 4 | Gray 5 | #3C3025 | 100% |
| Opacity 80% | Gray 4 | #3C3025 | 80% |
| Text 3 | Gray 3 | #9D9792 | 100% |
| Text 2 | Gray 2 | #CECBC8 | 100% |
| Text 1 | Gray 1 | #EBEAE9 | 100% |

### Alert / Accent
| Semantic | Palette | Hex |
|----------|---------|-----|
| Alert | Red | #FF6363 |
| Alert | Green | #02B57F |
| Accent | — | #00CC8F |

## Typography

본문 서체: **LeeSeoyun (이서윤체)**, weight 400, 국문/영문 통일 (디자인 명기: "이서윤체 국, 영문 통일").

| Style | Size(px) | LineHeight(px, 샘플 실측) | LetterSpacing(샘플) |
|-------|----------|---------------------------|---------------------|
| H1 | 24 | 24.0 | 0 |
| H2 | 20 | 22.37 | 0 |
| H3 | 18 | 20.13 | 0 |
| H4 | 16 | 16.0 | 0 |
| Body 1 | 14 | 14.0 | -0.3 |
| Body 2 | 13 | 20.0 | -0.3 |
| Detail (Caption) | 12 | 12.0 | -0.3 |

> ⚠️ 디자인 주석 "*All letter spacing 0px"와 샘플 실측값(-0.3, Body/Detail)이 상충.

## Spacing

4, 8, 12, 16, 20, 24, 32, 40 (px)

## Icon

24x24 그리드, 총 24종. 원본 주석: **"*확정 x 예시로 넣었습니다"** — 아이콘 세트는 미확정.

- `ic/` 네임드: share, list, arrow/left, arrow/right, settings, loading, check, more, download
- 외부 라이브러리 참조명: heroicons-solid:camera, tabler:pencil-plus, ci:edit-pencil-line-01/02, ic:round-arrow-left, eva:arrow-up-fill, octicon:plus-16, ci:hamburger-md, lucide:x, ph:camera-fill, uis:exclamation-circle, ri:pencil-fill, majesticons:mail, fluent:info-24-filled

## Style Guide 화면 구성 (node 836:33127 실측 — 확인용 쇼케이스 화면의 근거)

프레임 900x2800, 상→하 섹션: Title → Font → Text Style → Color → Icon → Spacing

- **Title**: "Style Guide" (대제목) + "Sikdorok" 라벨
- **Font**: 회색 박스(#F9F9F9) 안에 샘플 "매일 먹는 도시락, 식도록과 함께 간편하게 기록하세요!" + 캡션 "이서윤체 국, 영문 통일"
- **Text Style** 샘플 텍스트 (전부 이서윤체):
  - H1/24: "식도록과 함께하는" / H2/20: "6월의 도시락" / H3/18: "오늘의 도시락" / H4/16: "저녁"
  - Body1/14: "오늘의 메뉴" / Body2/13: "메모를 남겨보세요" / Detail/12: "오후 12:53"
- **Color**: 컬러칩 카드(50x50 스와치 + 역할명 + 팔레트명 + hex), 그룹 라벨 Bg / Txt·Btn / Alert
- **Icon**: 24종 — 쇼케이스에서 제외 (Q2=B, 디자인 미확정)
- **Spacing**: 높이 4/8/12/16/20/24/32/40 가로 바(#444444) + 수치 라벨

# Application Design — Component Dependency (design-system)

```
androidApp ----> shared ----> core:designsystem
iosApp     ----> shared (iOS framework "Shared") ----> core:designsystem
```

텍스트 설명: `:androidApp`과 iosApp은 기존대로 `:shared`에만 의존하고, `:shared`가 신규 `:core:designsystem`에 의존한다. `:core:designsystem`은 어떤 프로젝트 모듈에도 의존하지 않는다(compose 라이브러리만 의존).

- 순환 의존성: 없음 (단방향 체인)
- iOS 노출: iosApp은 `MainViewController`만 호출하므로 designsystem 타입의 framework export 불필요 (static framework에 전이 포함)

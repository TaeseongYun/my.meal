package com.devts.mymeal.feature.settings

import androidx.compose.runtime.Immutable

/**
 * 설정 화면 상태. 레이아웃은 Figma 333:3518, 항목 구성은 2026-09-09 재정의
 * (목업의 "원본 사진 자동 저장" 플레이스홀더 4행 → 정보/계정 섹션).
 * 실제 계정 정보·버전·업데이트 확인은 F-7 소관이며 그 전까지 [stubSettingsUiState]가 공급한다.
 */
@Immutable
data class SettingsUiState(
    val profile: SettingsProfile,
    val version: String,
    /** 스토어에 상위 버전이 있으면 버전 행이 스토어 이동 행으로 바뀐다 (Android는 Play In-App Updates). */
    val updateAvailable: Boolean = false,
)

@Immutable
data class SettingsProfile(val nickname: String, val email: String)

/** 프리뷰·UI 슬라이스 전용. 버전은 androidApp versionName("1.0")과 맞춘 값. */
fun stubSettingsUiState(): SettingsUiState = SettingsUiState(
    profile = SettingsProfile(nickname = "닉네임닉네임", email = "sikdorok.ddd@gmail.com"),
    version = "1.0",
)

import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

// Supabase URL/키는 저장소에 넣지 않는다 (ctx: Forbidden Decisions) — local.properties에서 주입.
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use(::load)
}
val supabaseUrl: String = localProperties.getProperty("supabase.url").orEmpty().trim()
val supabaseAnonKey: String = localProperties.getProperty("supabase.anonKey").orEmpty().trim()
val kakaoNativeAppKey: String = localProperties.getProperty("kakao.nativeAppKey").orEmpty().trim()

val generateAuthConfig = tasks.register("generateAuthConfig") {
    val outputDir = layout.buildDirectory.dir("generated/authConfig")
    // 설정 캐시: 스크립트 객체 참조를 캡처하지 않도록 로컬로 복사
    val url = supabaseUrl
    val anonKey = supabaseAnonKey
    val kakaoKey = kakaoNativeAppKey
    inputs.property("supabaseUrl", url)
    inputs.property("supabaseAnonKey", anonKey)
    inputs.property("kakaoNativeAppKey", kakaoKey)
    outputs.dir(outputDir)
    doLast {
        val dir = outputDir.get().asFile.resolve("com/devts/mymeal/core/auth")
        dir.mkdirs()
        dir.resolve("AuthConfig.kt").writeText(
            """
            package com.devts.mymeal.core.auth

            // 생성 파일 — local.properties에서 주입된다. 직접 수정하지 말 것.
            internal object AuthConfig {
                const val SUPABASE_URL: String = "$url"
                const val SUPABASE_ANON_KEY: String = "$anonKey"
                const val KAKAO_NATIVE_APP_KEY: String = "$kakaoKey"
            }

            /** 카카오 SDK 초기화에 필요 — 플랫폼 어댑터가 쓴다. */
            val kakaoNativeAppKey: String get() = AuthConfig.KAKAO_NATIVE_APP_KEY

            """.trimIndent(),
        )
    }
}

kotlin {
    applyDefaultHierarchyTemplate()
    iosArm64()
    iosSimulatorArm64()

    android {
       namespace = "com.devts.mymeal.core.auth"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()

       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       withHostTest { }
    }

    sourceSets {
        commonMain {
            kotlin.srcDir(generateAuthConfig)
            dependencies {
                // 공개 API에 supabase 타입을 노출하지 않는다 (AuthRepository/AuthTokens만)
                implementation(libs.supabase.auth)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

rootProject.name = "Mymeal"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven("https://devrepo.kakao.com/nexus/content/groups/public/") {
            content { includeGroup("com.kakao.sdk") } // 카카오 SDK는 Maven Central에 없음
        }
    }
}

include(":androidApp")
include(":shared")
include(":core:designsystem")
include(":catalogApp")
include(":feature:login")
include(":core:model")
include(":core:data")
include(":feature:home")
include(":feature:record")
include(":core:auth")
include(":feature:settings")

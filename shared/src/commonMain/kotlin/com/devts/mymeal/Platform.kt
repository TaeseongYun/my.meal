package com.devts.mymeal

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
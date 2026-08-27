package com.devts.mymeal.catalog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.devts.mymeal.core.designsystem.SikdorokTheme
import com.devts.mymeal.core.designsystem.StyleGuideScreen

class CatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            SikdorokTheme {
                StyleGuideScreen()
            }
        }
    }
}

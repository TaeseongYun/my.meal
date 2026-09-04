package com.devts.mymeal.feature.record

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

// TakePicture는 시스템 카메라 앱 위임이라 CAMERA 권한 불요(Manifest 미선언 유지),
// PickVisualMedia는 시스템 포토피커라 저장소 권한 불요.
@Composable
actual fun rememberPhotoPicker(onPicked: (ByteArray) -> Unit): PhotoPicker {
    val context = LocalContext.current

    fun readBytes(uri: Uri) {
        context.contentResolver.openInputStream(uri)?.use { onPicked(it.readBytes()) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(::readBytes)
    }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        if (saved) cameraUri?.let(::readBytes)
    }

    return remember {
        PhotoPicker(
            launchCamera = {
                val dir = File(context.cacheDir, "capture").apply { mkdirs() }
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    File(dir, "capture.jpg"), // ponytail: 단일 임시 파일 — 촬영마다 덮어씀
                )
                cameraUri = uri
                cameraLauncher.launch(uri)
            },
            launchGallery = {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
        )
    }
}

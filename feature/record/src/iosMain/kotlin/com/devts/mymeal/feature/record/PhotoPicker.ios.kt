package com.devts.mymeal.feature.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.memcpy

// ponytail: 촬영·앨범 모두 UIImagePickerController — PHPicker 전환은 photoLibrary 소스 중단 시.
// NSCameraUsageDescription은 iosApp Info.plist에 선언.
@Composable
actual fun rememberPhotoPicker(onPicked: (ByteArray) -> Unit): PhotoPicker {
    val delegate = remember { ImagePickerDelegate(onPicked) }
    return remember {
        PhotoPicker(
            launchCamera = { delegate.present(camera = true) },
            launchGallery = { delegate.present(camera = false) },
        )
    }
}

private class ImagePickerDelegate(
    private val onPicked: (ByteArray) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    fun present(camera: Boolean) {
        val cameraType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        val picker = UIImagePickerController().apply {
            sourceType = if (camera && UIImagePickerController.isSourceTypeAvailable(cameraType)) {
                cameraType // 시뮬레이터는 카메라 없음 → 앨범 폴백
            } else {
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            }
            delegate = this@ImagePickerDelegate
        }
        rootViewController()?.presentViewController(picker, animated = true, completion = null)
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        picker.dismissViewControllerAnimated(true, completion = null)
        val data = image?.let { UIImageJPEGRepresentation(it, 0.9) } ?: return
        onPicked(data.toByteArray())
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
    }
}

private fun rootViewController(): UIViewController? =
    UIApplication.sharedApplication.keyWindow?.rootViewController

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val result = ByteArray(size)
    result.usePinned { memcpy(it.addressOf(0), bytes, length) }
    return result
}

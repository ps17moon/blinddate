package com.example.blinddate.camera

import android.content.Context
import android.util.Size
import android.view.TextureView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.blinddate.face.FaceDetectionProcessor
import com.example.blinddate.overlay.EmojiOverlayView
import java.util.concurrent.Executors

class CameraManager(
    private val context: Context,
    private val previewView: TextureView,
    private val emojiOverlayView: EmojiOverlayView,
    private val onFrameReady: (bitmap: android.graphics.Bitmap) -> Unit // ✅ 추가
) {
    private val executor = Executors.newSingleThreadExecutor()

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .setTargetResolution(Size(640, 480))
                .build()
                .also {
                    it.setSurfaceProvider(SurfaceTextureProvider.createSurfaceTextureProvider(previewView))
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        executor,
                        FaceDetectionProcessor(context, emojiOverlayView, onFrameReady) // ✅ 전달
                    )
                }

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(context))
    }
}

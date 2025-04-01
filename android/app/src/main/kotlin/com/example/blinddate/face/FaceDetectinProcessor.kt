package com.example.blinddate.face

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.blinddate.overlay.EmojiOverlayView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectionProcessor(
    private val context: Context,
    private val emojiOverlayView: EmojiOverlayView,
    private val onFrameReady: (Bitmap) -> Unit // ✅ 추가
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .build()
    )

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)

        detector.process(image)
            .addOnSuccessListener { faces ->
                val faceRects = faces.map { it.boundingBox }

                // ✅ 이모지 업데이트
                emojiOverlayView.updateFaces(faceRects)

                // ✅ 현재 EmojiOverlayView를 Bitmap으로 캡처해서 전달
                emojiOverlayView.post {
                    val bitmap = Bitmap.createBitmap(
                        emojiOverlayView.width,
                        emojiOverlayView.height,
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = android.graphics.Canvas(bitmap)
                    emojiOverlayView.draw(canvas)

                    // ✅ AgoraVideoSource로 전달!
                    onFrameReady(bitmap)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FaceDetection", "얼굴 감지 실패: ${e.message}")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

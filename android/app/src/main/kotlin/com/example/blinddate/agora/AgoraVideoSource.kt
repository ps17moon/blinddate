package com.example.blinddate.agora

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import io.agora.base.VideoFrame
import io.agora.base.VideoFrame.TextureBuffer.Type
import io.agora.base.internal.video.EglBaseProvider
import io.agora.rtc2.video.VideoFrameConsumer
import io.agora.rtc2.video.VideoSource

class AgoraVideoSource : VideoSource {

    private var consumer: VideoFrameConsumer? = null
    private var isCapturing = false
    private lateinit var handler: Handler

    override fun onInitialize(context: android.content.Context?, eglContext: EglBaseProvider?): Boolean {
        val thread = HandlerThread("AgoraFrameThread")
        thread.start()
        handler = Handler(thread.looper)
        return true
    }

    override fun onStart(): Boolean {
        isCapturing = true
        return true
    }

    override fun onStop(): Boolean {
        isCapturing = false
        return true
    }

    override fun onDispose() {
        handler.looper.quitSafely()
    }

    override fun getCaptureType(): Int = VideoSource.CAPTURE_TYPE_CUSTOM

    override fun onConsumeVideoFrame(consumer: VideoFrameConsumer?) {
        this.consumer = consumer
    }

    override fun getContentHint(): Int = VideoSource.CONTENT_HINT_MOTION

    // ✅ 외부에서 호출: 이모지 오버레이가 적용된 비트맵 프레임 전달
    fun sendFrame(bitmap: Bitmap) {
        if (!isCapturing || consumer == null) return

        val width = bitmap.width
        val height = bitmap.height

        // ✅ Bitmap을 ARGB 배열로 추출
        val argb = IntArray(width * height)
        bitmap.getPixels(argb, 0, width, 0, 0, width, height)

        // ✅ ARGB to NV21 (YUV420) 변환
        val nv21 = convertARGBToNV21(argb, width, height)

        // ✅ Agora에 프레임 전송
        consumer?.consumeByteArrayFrame(
            nv21,
            VideoFrame.Format.NV21.value,
            width,
            height,
            0, // rotation
            System.currentTimeMillis()
        )
    }

        private fun convertARGBToNV21(argb: IntArray, width: Int, height: Int): ByteArray {
        val yuv = ByteArray(width * height * 3 / 2)
        var yIndex = 0
        var uvIndex = width * height

        for (j in 0 until height) {
            for (i in 0 until width) {
                val rgb = argb[j * width + i]

                val r = (rgb shr 16) and 0xFF
                val g = (rgb shr 8) and 0xFF
                val b = rgb and 0xFF

                val y = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
                val u = (-0.169 * r - 0.331 * g + 0.5 * b + 128).toInt()
                val v = (0.5 * r - 0.419 * g - 0.081 * b + 128).toInt()

                yuv[yIndex++] = y.coerceIn(0, 255).toByte()

                if (j % 2 == 0 && i % 2 == 0) {
                    yuv[uvIndex++] = v.coerceIn(0, 255).toByte()
                    yuv[uvIndex++] = u.coerceIn(0, 255).toByte()
                }
            }
        }

        return yuv
    }
}

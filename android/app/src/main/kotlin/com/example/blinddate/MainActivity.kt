package com.example.blinddate

import android.os.Bundle
import android.view.TextureView
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.blinddate.agora.AgoraVideoSource
import com.example.blinddate.camera.CameraManager
import com.example.blinddate.overlay.EmojiOverlayView
import com.example.blinddate.util.ProfileImageUtil
import com.example.blinddate.face.FaceComparer
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.agora.rtc2.*
import android.graphics.Bitmap
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var agoraEngine: RtcEngine
    private lateinit var videoSource: AgoraVideoSource
    private lateinit var emojiOverlay: EmojiOverlayView
    private lateinit var methodChannel: MethodChannel


    private var latestFaceBitmap: Bitmap? = null // ✅ 가장 최근 얼굴 프레임 저장용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        flutterEngine?.let { configureFlutterEngine(it) }

        val previewView = TextureView(this)
        emojiOverlay = EmojiOverlayView(this)

        val container = FrameLayout(this)
        container.addView(previewView)
        container.addView(emojiOverlay)

        // ✅ 인증 버튼 추가
        val verifyButton = Button(this).apply {
            text = "얼굴 인증"
            setOnClickListener {
                latestFaceBitmap?.let {
                    performFaceVerification(it)
                }
            }
        }
        container.addView(verifyButton)

        setContentView(container)

        initAgora()

        // ✅ 영상 프레임 처리 + 최신 bitmap 저장
        cameraManager = CameraManager(this, previewView, emojiOverlay, onFrameReady = { bitmap ->
            videoSource.sendFrame(bitmap)
            latestFaceBitmap = bitmap // ✅ 인증용으로 저장
        })

        cameraManager.startCamera()
    }

    private fun initAgora() {
        val config = RtcEngineConfig()
        config.mContext = this
        config.mAppId = "190fb9431d2646dba18d5c0a5aeaa41f"
        config.mEventHandler = object : IRtcEngineEventHandler() {}

        agoraEngine = RtcEngine.create(config)

        videoSource = AgoraVideoSource()
        agoraEngine.setVideoSource(videoSource)

        agoraEngine.enableVideo()
        agoraEngine.joinChannel(null, "test_channel", "", 0)
    }

    // ✅ 인증 실행 함수
    private fun performFaceVerification(liveFaceBitmap: Bitmap) {
        val currentUser = Firebase.auth.currentUser ?: return
        val uid = currentUser.uid

        ProfileImageUtil.getProfileImage(uid) { profileBitmap ->
            if (profileBitmap != null) {
                val result = FaceComparer().compareFaces(profileBitmap, liveFaceBitmap)
                runOnUiThread {
                    if (result) {
                        emojiOverlay.clearOverlay()
                        methodChannel.invokeMethod("onVerifyResult", "success") // ✅ 성공
                    } else {
                        Toast.makeText(this, "얼굴 인증에 실패했습니다 😢", Toast.LENGTH_SHORT).show()
                        methodChannel.invokeMethod("onVerifyResult", "fail") // ✅ 실패
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "프로필 사진을 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                    methodChannel.invokeMethod("onVerifyResult", "no_profile") // ✅ 사진 없음
                }
            }
        }
    }


    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        methodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            "com.blinddate/face_verify" // 🔑 채널 이름은 Flutter와 일치해야 함
        )
    }

}

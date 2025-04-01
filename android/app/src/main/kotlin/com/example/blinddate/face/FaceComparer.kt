package com.example.blinddate.face

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class FaceComparer {

    private val client = OkHttpClient()
    private val serverUrl = "http://YOUR_API_SERVER/verify" // ❗️DeepFace API 서버 주소

    fun compareFaces(face1: Bitmap, face2: Bitmap): Boolean {
        try {
            val base64Face1 = bitmapToBase64(face1)
            val base64Face2 = bitmapToBase64(face2)

            val json = JSONObject().apply {
                put("face1", base64Face1)
                put("face2", base64Face2)
            }

            val body = RequestBody.create(
                "application/json".toMediaTypeOrNull(),
                json.toString()
            )

            val request = Request.Builder()
                .url(serverUrl)
                .post(body)
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val resultJson = JSONObject(response.body?.string() ?: "")
                return resultJson.optBoolean("result", false)
            } else {
                Log.e("FaceComparer", "API 요청 실패: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("FaceComparer", "에러: ${e.message}")
        }

        return false // 실패 시 무조건 false
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}

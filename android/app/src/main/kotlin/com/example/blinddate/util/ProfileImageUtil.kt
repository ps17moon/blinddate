package com.example.blinddate.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayInputStream

object ProfileImageUtil {

    fun getProfileImage(uid: String, onResult: (Bitmap?) -> Unit) {
        val storageRef = Firebase.storage.reference.child("profile_images/$uid.jpg")

        storageRef.getBytes(1024 * 1024) // 최대 1MB
            .addOnSuccessListener { bytes ->
                val inputStream = ByteArrayInputStream(bytes)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                Log.d("ProfileImageUtil", "프로필 이미지 로딩 성공")
                onResult(bitmap)
            }
            .addOnFailureListener { e ->
                Log.e("ProfileImageUtil", "프로필 이미지 로딩 실패: ${e.message}")
                onResult(null)
            }
    }
}

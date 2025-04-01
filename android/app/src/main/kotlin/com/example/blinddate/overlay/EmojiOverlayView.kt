package com.example.blinddate.overlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.blinddate.R

class EmojiOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val faceRects = mutableListOf<Rect>()
    private val paint = Paint()
    private val emojiBitmap: Bitmap by lazy {
        val drawable = ContextCompat.getDrawable(context, R.drawable.emoji)
        val bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable?.setBounds(0, 0, canvas.width, canvas.height)
        drawable?.draw(canvas)
        bmp
    }

    fun updateFaces(faces: List<Rect>) {
        faceRects.clear()
        faceRects.addAll(faces)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (rect in faceRects) {
            canvas.drawBitmap(
                emojiBitmap,
                null,
                rect,
                paint
            )
        }
    }
}

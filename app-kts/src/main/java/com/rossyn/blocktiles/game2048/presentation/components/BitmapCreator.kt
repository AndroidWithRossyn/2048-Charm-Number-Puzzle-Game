package com.rossyn.blocktiles.game2048.presentation.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.rossyn.blocktiles.game2048.MyApplication
import com.rossyn.blocktiles.game2048.R
import com.rossyn.blocktiles.game2048.utils.getDrawableCompat
import kotlin.math.pow

class BitmapCreator {
    private val context: Context = MyApplication.getContext()
    private var mPaint = Paint()
    private var textBounds = Rect()

    companion object {
        var cellDefaultHeight: Int = 0
        var cellDefaultWidth: Int = 0
        var exponent: Int = 0
        var bitmapArrayList = ArrayList<Bitmap>()
    }


    fun getCellDefaultWidth(): Int {
        return cellDefaultWidth
    }

    fun getCellDefaultHeight(): Int {
        return cellDefaultHeight
    }

    fun createBlockTile(): Bitmap {
        val drawable = context.getDrawableCompat(R.drawable.block_shape)
        val bitmap =
            Bitmap.createBitmap(cellDefaultWidth, cellDefaultHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable?.let {
            it.setBounds(0, 0, cellDefaultWidth, cellDefaultHeight)
            it.draw(canvas)
        }
        return bitmap
    }

    fun createBitmap(index: Int) {
        val drawable = createDrawable(index)
        val value = exponent.toDouble().pow(index + 1).toLong()
        val text = value.toString()
        val bitmap =
            Bitmap.createBitmap(cellDefaultWidth, cellDefaultHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val customTypeface = ResourcesCompat.getFont(context, R.font.baloo)
        var textSize = 130f
        mPaint.apply {
            typeface = customTypeface
            color = Color.WHITE
            this.textSize = textSize
        }
        drawable.setBounds(0, 0, cellDefaultWidth, cellDefaultHeight)
        mPaint.getTextBounds(text, 0, text.length, textBounds)

        while (textBounds.height() > cellDefaultWidth / 2.5 && textSize >= 10) {
            textSize -= 20f
            mPaint.textSize = textSize.toFloat()
            mPaint.getTextBounds(text, 0, text.length, textBounds)
            mPaint.getTextBounds(text, 0, text.length, textBounds)
        }

        while (textBounds.width() > cellDefaultWidth - 20 && textSize >= 10) {
            textSize -= 10f
            mPaint.textSize = textSize.toFloat()
            mPaint.getTextBounds(text, 0, text.length, textBounds)
            mPaint.getTextBounds(text, 0, text.length, textBounds)
        }

        drawable.draw(canvas)
        canvas.drawText(
            text,
            (cellDefaultWidth / 2f - textBounds.exactCenterX()),
            (cellDefaultHeight / 2f - textBounds.exactCenterY()),
            mPaint
        )
        bitmapArrayList.add(bitmap)
    }

    fun getBitmap(value: Long): Bitmap {
        if (value == 1L) {
            return createBlockTile()
        }

        val logVal = Math.log(value.toDouble()) / Math.log(exponent.toDouble())
        val roundedVal = Math.round(logVal)
        val index = (roundedVal - 1).toInt()

        if (bitmapArrayList.isEmpty()) {
            for (i in 0..11) {
                createBitmap(i)
            }
        }

        if (index == bitmapArrayList.size) {
            createBitmap(index)
        }

        return bitmapArrayList[index]
    }

    fun clearBitmapArray() {
        bitmapArrayList.clear()
    }

    fun createDrawable(index: Int): Drawable {
        val drawable = context.getDrawableCompat(R.drawable.cell_shape)

        val colorRes = when (index) {
            0 -> R.color.value2
            1 -> R.color.value4
            2 -> R.color.value8
            3 -> R.color.value16
            4 -> R.color.value32
            5 -> R.color.value64
            6 -> R.color.value128
            7 -> R.color.value256
            8 -> R.color.value512
            9 -> R.color.value1024
            10 -> R.color.value2048
            else -> R.color.valueOther
        }

        drawable!!.setColorFilter(context.getColor(colorRes), PorterDuff.Mode.SRC_OVER)
        return drawable
    }
}

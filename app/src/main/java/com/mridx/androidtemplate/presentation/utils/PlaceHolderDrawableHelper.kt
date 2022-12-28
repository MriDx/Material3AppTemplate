package com.mridx.androidtemplate.presentation.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable

object PlaceHolderDrawableHelper {
    private val placeholderValues = arrayOf(
        "#76A599", "#7D9A96", "#6F8888",
        "#82A1AB", "#8094A1", "#74838C",
        "#B6A688", "#9E9481", "#8D8F9F",
        "#A68C93", "#968388", "#7E808B"
    )
    var drawableBackgroundList: MutableList<Drawable>? = null
    fun getBackgroundDrawable(position: Int): Drawable {
        if (drawableBackgroundList == null || drawableBackgroundList!!.size == 0) {
            drawableBackgroundList = ArrayList(placeholderValues.size)
            for (i in placeholderValues.indices) {
                val color: Int = Color.parseColor(placeholderValues[i])
                (drawableBackgroundList as ArrayList<Drawable>).add(ColorDrawable(color))
            }
        }
        return drawableBackgroundList!![position % placeholderValues.size]
    }

    private var staticDrawable: Drawable? = null
    fun getPlaceHolderDrawable(): Drawable {
        if (staticDrawable == null)
            staticDrawable = ColorDrawable(Color.parseColor("#f8fafc"))
        return staticDrawable!!
    }

    fun ImageView.getAvatar(context: Context, name: String, position: Int = 0): Drawable {
        if (drawableBackgroundList == null || drawableBackgroundList!!.size == 0) {
            drawableBackgroundList = ArrayList(placeholderValues.size)
            for (i in placeholderValues.indices) {
                val color: Int = Color.parseColor(placeholderValues[i])
                (drawableBackgroundList as ArrayList<Drawable>).add(ColorDrawable(color))
            }
        }
        val d = drawableBackgroundList!![position % placeholderValues.size]
        val bitmap = d.toBitmap(width, height)
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = width * .5f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
        }
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = textHeight / 2 - textPaint.descent()
        val toPaint = name.first().toString()
        return bitmap.applyCanvas {
            drawText(
                toPaint,
                (width / 2f),
                (height / 2f) + textOffset,
                textPaint
            )
        }.toDrawable(resources = context.resources)
    }

    fun getAvatar(context: Context, name: String, position: Int = 0): Drawable {
        val width = 100
        val height = 100
        if (drawableBackgroundList == null || drawableBackgroundList!!.size == 0) {
            drawableBackgroundList = ArrayList(placeholderValues.size)
            for (i in placeholderValues.indices) {
                val color: Int = Color.parseColor(placeholderValues[i])
                (drawableBackgroundList as ArrayList<Drawable>).add(ColorDrawable(color))
            }
        }
        val d = drawableBackgroundList!![position % placeholderValues.size]
        val bitmap = d.toBitmap(width, height)
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = width * .5f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
        }
        val textHeight = textPaint.descent() - textPaint.ascent()
        val textOffset = textHeight / 2 - textPaint.descent()
        val toPaint = name.first().toString()
        return bitmap.applyCanvas {
            drawText(
                toPaint,
                (width / 2f),
                (height / 2f) + textOffset,
                textPaint
            )
        }.toDrawable(resources = context.resources)
    }
}
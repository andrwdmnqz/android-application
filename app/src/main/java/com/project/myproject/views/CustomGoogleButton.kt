package com.project.myproject.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.project.myproject.R

class CustomGoogleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var buttonText: String
    private var buttonTextSize: Float = 0f
    private var buttonLetterSpacing: Float = 0f
    private var buttonTextColor: Int = 0
    private var buttonDrawable: Drawable? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomGoogleButton)

        buttonText = typedArray.getString(R.styleable.CustomGoogleButton_buttonText) ?: ""
        buttonTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomGoogleButton_buttonTextSize,
            resources.getDimensionPixelSize(R.dimen.h2_5_text)).toFloat()
        buttonLetterSpacing = typedArray.getFloat(R.styleable.CustomGoogleButton_buttonLetterSpacing,
            0.0f)
        buttonTextColor = typedArray.getColor(R.styleable.CustomGoogleButton_buttonTextColor,
            ContextCompat.getColor(context, R.color.additional_text_first))
        buttonDrawable = typedArray.getDrawable(R.styleable.CustomGoogleButton_buttonDrawable)

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = buttonTextColor
        paint.textSize = buttonTextSize
        paint.letterSpacing = buttonLetterSpacing

        val textWidth = paint.measureText(buttonText)
        val textHeight = paint.descent() - paint.ascent()

        val x = (width - textWidth) / 2f
        val y = (height - textHeight) / 2f - paint.ascent()

        canvas.drawText(buttonText, x, y, paint)
        buttonDrawable?.draw(canvas)
    }
}
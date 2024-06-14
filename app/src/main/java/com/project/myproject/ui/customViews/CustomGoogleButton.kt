package com.project.myproject.ui.customViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.project.myproject.R


private const val IMAGE_TEXT_MARGIN = 14

class CustomGoogleButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr) {

    private var buttonText: String
    private lateinit var typeface: Typeface
    private var buttonTextSize: Float = 0f
    private var buttonLetterSpacing: Float = 0f
    private var buttonTextColor: Int = 0
    private var buttonDrawable: Drawable? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomGoogleButton)

        buttonText = typedArray.getString(R.styleable.CustomGoogleButton_buttonText) ?: ""

        val fontFamilyId = typedArray.getResourceId(R.styleable.CustomGoogleButton_android_fontFamily, 0)
        if (fontFamilyId > 0) {
            typeface = ResourcesCompat.getFont(getContext(), fontFamilyId)!!
        }

        buttonTextSize = typedArray.getDimensionPixelSize(
            R.styleable.CustomGoogleButton_buttonTextSize,
            resources.getDimensionPixelSize(R.dimen.h2_5_text)
        ).toFloat()
        buttonLetterSpacing = typedArray.getFloat(
            R.styleable.CustomGoogleButton_buttonLetterSpacing,
            0.0f
        )
        buttonTextColor = typedArray.getColor(
            R.styleable.CustomGoogleButton_buttonTextColor,
            ContextCompat.getColor(context, R.color.additional_fifth)
        )
        buttonDrawable = typedArray.getDrawable(R.styleable.CustomGoogleButton_buttonDrawable)

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val textWidth = paint.measureText(buttonText)
        val imageTextMargin = dpToPx(IMAGE_TEXT_MARGIN, context)
        val drawableWidth = buttonDrawable?.intrinsicWidth ?: 0
        val drawableLeft = ((width - (textWidth + drawableWidth + imageTextMargin)) / 2f).toInt()

        setupPaint()
        drawDrawable(canvas, drawableWidth, drawableLeft)
        drawText(canvas, imageTextMargin, drawableWidth, drawableLeft)
    }

    private fun setupPaint() {
        paint.color = buttonTextColor
        paint.textSize = buttonTextSize
        paint.letterSpacing = buttonLetterSpacing
    }

    private fun drawDrawable(canvas: Canvas, drawableWidth: Int, drawableLeft: Int) {

        val drawableHeight = buttonDrawable?.intrinsicHeight ?: 0
        val drawableTop = (height - drawableHeight) / 2

        buttonDrawable?.let {
            it.setBounds(
                drawableLeft,
                drawableTop,
                drawableLeft + drawableWidth,
                drawableTop + drawableHeight
            )
            it.draw(canvas)
        }
    }

    private fun drawText(canvas: Canvas, imageTextMargin: Int, drawableWidth: Int, drawableLeft: Int) {

        val textHeight = paint.descent() - paint.ascent()

        val textLeft = (drawableLeft + drawableWidth + imageTextMargin).toFloat()
        val textTop = (height + textHeight) / 2 - paint.descent()

        canvas.drawText(buttonText, textLeft, textTop, paint)
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
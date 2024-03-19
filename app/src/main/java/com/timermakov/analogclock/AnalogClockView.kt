package com.timermakov.analogclock

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnalogClockView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    data class ClockNumber(val value: String, val x: Float, val y: Float)
    private val numbersList = mutableListOf<ClockNumber>()

    private val rimPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 20f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val centerPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val smallDotPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val bigDotPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 50f
        isAntiAlias = true
    }
    private val hourPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        isAntiAlias = true
    }
    private val minutePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
        isAntiAlias = true
    }
    private val secondPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Rim
        val radius = min(width, height) / 2f
        canvas.drawCircle(width / 2f, height / 2f, radius - rimPaint.strokeWidth, rimPaint)

        // Center
        canvas.drawCircle(width / 2f, height / 2f, centerPaint.strokeWidth, centerPaint)

        // Dots
        for (i in 1..60) {
            val angle = 2 * Math.PI / 60 * (i)
            val x = (width / 2 + cos(angle) * radius * 0.8).toFloat()
            val y = (height / 2 + sin(angle) * radius * 0.8).toFloat()
            if (i % 5 == 0) canvas.drawCircle(x, y, bigDotPaint.strokeWidth, bigDotPaint)
                else canvas.drawCircle(x, y, smallDotPaint.strokeWidth, smallDotPaint)
        }

        // Numbers
        numbersList.forEach { number ->
            canvas.drawText(number.value, number.x, number.y, textPaint)
        }

        // Current time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        // Hands
        drawHand(canvas, (hour + minute / 60f) * 5f, true, hourPaint)
        drawHand(canvas, minute.toFloat(), false, minutePaint)
        drawHand(canvas, second.toFloat(), false, secondPaint)

        // Update every second
        postInvalidateDelayed(1000)
    }

    private fun drawHand(canvas: Canvas, moment: Float, isHour: Boolean, paint: Paint) {
        val angle = Math.PI * moment / 30 - Math.PI / 2
        val handRadius = if (isHour) width / 5f else width / 4f
        canvas.drawLine(
            width / 2f,
            height / 2f,
            (width / 2 + cos(angle) * handRadius).toFloat(),
            (height / 2 + sin(angle) * handRadius).toFloat(),
            paint
        )
    }

    private fun initNumbers(radius: Float) {
        numbersList.clear()

        textPaint.textSize = radius / 5f
        textPaint.textAlign = Paint.Align.CENTER

        for (number in 1..12) {
            val text = number.toString()
            val textBounds = Rect()
            textPaint.getTextBounds(text, 0, text.length, textBounds)

            val angle = Math.PI / 6 * (number - 3)

            // Text location adustment
            val x = (width / 2f + cos(angle) * (radius - textBounds.width() / 2f)).toFloat()
            val y = (height / 2f + sin(angle) * (radius - textBounds.width() / 2f) +
                    textBounds.height() / 2f - textBounds.bottom).toFloat()

            numbersList.add(ClockNumber(text, x, y))
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rimPaint.strokeWidth = min(w, h) / 20f
        centerPaint.strokeWidth = min(w, h) / 40f
        bigDotPaint.strokeWidth = min(w, h) / 100f
        smallDotPaint.strokeWidth = min(w, h) / 200f

        textPaint.textSize = min(w, h) / 8f

        hourPaint.strokeWidth = min(w, h) / 40f
        minutePaint.strokeWidth = min(w, h) / 50f
        secondPaint.strokeWidth = min(w, h) / 100f

        val numbersRadius = min(w, h) / 2f - textPaint.textSize * 1.2f
        initNumbers(numbersRadius)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }
}

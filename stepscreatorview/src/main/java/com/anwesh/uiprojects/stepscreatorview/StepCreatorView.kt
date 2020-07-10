package com.anwesh.uiprojects.stepscreatorview

/**
 * Created by anweshmishra on 10/07/20.
 */

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val colors : Array<String> = arrayOf("#673AB7", "#F44336", "#00BCD4", "#4CAF50", "#2196F3")
val lines : Int = 4
val parts : Int = 3
val scGap : Float = 0.02f / (lines * parts)
val strokeFactor : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
val delay : Long = 20

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawStepLine(i : Int, scale : Float, w : Float, h : Float, paint : Paint) {
    val wGap : Float = w / (lines)
    val hGap : Float = h / (lines)
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, (parts + 1)).divideScale(i, lines)
    val sf2 : Float = sf.divideScale(1, (parts + 1)).divideScale(i, lines)
    val sf3 : Float = sf.divideScale(2, (parts + 1)).divideScale(i, lines)
    save()
    translate(wGap * i, hGap * (i + 1) * sf2)
    drawLine(0f, 0f, wGap * sf1, 0f, paint)
    drawLine(wGap, -hGap * (i + 1) * sf3, 0f, 0f, paint)
    restore()
}

fun Canvas.drawStepLines(scale : Float, w : Float, h : Float, paint : Paint) {
    for (j in 0..(lines - 1)) {
        drawStepLine(j, scale, w, h, paint)
    }
}

fun Canvas.drawSCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawStepLines(scale, w, h, paint)
}

class StepCreatorView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}
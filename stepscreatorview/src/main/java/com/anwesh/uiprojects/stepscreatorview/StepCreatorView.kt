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

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class SCNode(var i : Int, val state : State = State()) {

        private var next : SCNode? = null
        private var prev : SCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = SCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawSCNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : SCNode {
            var curr : SCNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class StepCreator(var i : Int) {

        private var curr : SCNode = SCNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : StepCreatorView) {

        private val animator : Animator = Animator(view)
        private val sc : StepCreator = StepCreator(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            sc.draw(canvas, paint)
            animator.animate {
                sc.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            sc.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : StepCreatorView {
            val view : StepCreatorView = StepCreatorView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
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

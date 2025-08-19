package com.example.appdoro

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.random.Random

class DoroView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 表情状态枚举
    enum class Emotion {
        HAPPY,    // 开心
        ANGRY,    // 生气
        SAD,      // 伤心
        SURPRISED // 惊讶
    }

    private var currentEmotion = Emotion.HAPPY
    private var eyeOffsetX = 0f
    private var eyeOffsetY = 0f
    
    // 画笔
    private val facePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mouthPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // 动画相关
    private val handler = Handler(Looper.getMainLooper())
    private var animationRunnable: Runnable? = null
    private var eyeAnimationRunnable: Runnable? = null
    
    // 尺寸相关
    private var centerX = 0f
    private var centerY = 0f
    private var faceRadius = 0f
    private var eyeRadius = 0f
    
    init {
        setupPaints()
        startRandomEmotionChange()
        startEyeMovement()
    }
    
    private fun setupPaints() {
        facePaint.apply {
            color = ContextCompat.getColor(context, R.color.doro_face)
            style = Paint.Style.FILL
        }
        
        eyePaint.apply {
            color = ContextCompat.getColor(context, R.color.doro_eye)
            style = Paint.Style.FILL
        }
        
        mouthPaint.apply {
            style = Paint.Style.STROKE
            strokeWidth = 8f
            strokeCap = Paint.Cap.ROUND
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        faceRadius = minOf(w, h) * 0.3f
        eyeRadius = faceRadius * 0.08f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFace(canvas)
        drawEyes(canvas)
        drawMouth(canvas)
    }
    
    private fun drawFace(canvas: Canvas) {
        // 绘制脸部圆形
        canvas.drawCircle(centerX, centerY, faceRadius, facePaint)
    }
    
    private fun drawEyes(canvas: Canvas) {
        val eyeY = centerY - faceRadius * 0.2f
        val eyeDistance = faceRadius * 0.3f
        
        // 左眼
        val leftEyeX = centerX - eyeDistance + eyeOffsetX
        val leftEyeY = eyeY + eyeOffsetY
        canvas.drawCircle(leftEyeX, leftEyeY, eyeRadius, eyePaint)
        
        // 右眼
        val rightEyeX = centerX + eyeDistance + eyeOffsetX
        val rightEyeY = eyeY + eyeOffsetY
        canvas.drawCircle(rightEyeX, rightEyeY, eyeRadius, eyePaint)
    }
    
    private fun drawMouth(canvas: Canvas) {
        val mouthY = centerY + faceRadius * 0.2f
        val mouthWidth = faceRadius * 0.4f
        
        when (currentEmotion) {
            Emotion.HAPPY -> {
                mouthPaint.color = ContextCompat.getColor(context, R.color.doro_mouth_happy)
                // 画笑脸弧线
                val rect = RectF(
                    centerX - mouthWidth,
                    mouthY - mouthWidth * 0.3f,
                    centerX + mouthWidth,
                    mouthY + mouthWidth * 0.3f
                )
                canvas.drawArc(rect, 0f, 180f, false, mouthPaint)
            }
            Emotion.SAD -> {
                mouthPaint.color = ContextCompat.getColor(context, R.color.doro_mouth_sad)
                // 画伤心弧线
                val rect = RectF(
                    centerX - mouthWidth,
                    mouthY - mouthWidth * 0.3f,
                    centerX + mouthWidth,
                    mouthY + mouthWidth * 0.3f
                )
                canvas.drawArc(rect, 180f, 180f, false, mouthPaint)
            }
            Emotion.ANGRY -> {
                mouthPaint.color = Color.RED
                // 画生气的直线嘴
                canvas.drawLine(
                    centerX - mouthWidth * 0.5f,
                    mouthY,
                    centerX + mouthWidth * 0.5f,
                    mouthY,
                    mouthPaint
                )
            }
            Emotion.SURPRISED -> {
                mouthPaint.color = Color.BLACK
                // 画惊讶的圆形嘴
                canvas.drawCircle(centerX, mouthY, mouthWidth * 0.2f, mouthPaint)
            }
        }
    }
    
    private fun startRandomEmotionChange() {
        animationRunnable = object : Runnable {
            override fun run() {
                // 随机切换表情，3-8秒间隔
                val delay = Random.nextLong(3000, 8000)
                currentEmotion = Emotion.values()[Random.nextInt(Emotion.values().size)]
                invalidate()
                handler.postDelayed(this, delay)
            }
        }
        handler.postDelayed(animationRunnable!!, 2000)
    }
    
    private fun startEyeMovement() {
        eyeAnimationRunnable = object : Runnable {
            override fun run() {
                // 随机眼球转动
                val maxOffset = eyeRadius * 0.3f
                eyeOffsetX = Random.nextFloat() * maxOffset * 2 - maxOffset
                eyeOffsetY = Random.nextFloat() * maxOffset * 2 - maxOffset
                invalidate()
                
                // 1-3秒间隔
                val delay = Random.nextLong(1000, 3000)
                handler.postDelayed(this, delay)
            }
        }
        handler.postDelayed(eyeAnimationRunnable!!, 1000)
    }
    
    fun startAnimation() {
        startRandomEmotionChange()
        startEyeMovement()
    }
    
    fun stopAnimation() {
        animationRunnable?.let { handler.removeCallbacks(it) }
        eyeAnimationRunnable?.let { handler.removeCallbacks(it) }
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }
}
package unipi.sam.emusic.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import unipi.sam.emusic.R


class CustomView : View {
        private var mRoundPercent = 0f // rounds the corners as a percent
        private var mRound = Float.NaN // rounds the corners in dp if NaN RoundPercent is in effect
        private var mPath: Path? = null
        var mViewOutlineProvider: ViewOutlineProvider? = null
        var mRect: RectF? = null

        constructor(context: Context) : super(context) {
            init(context, null)
        }

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
            init(context, attrs)
        }

        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        ) {
            init(context, attrs)
        }
        private fun init(context: Context, attrs: AttributeSet?) {
            setPadding(0, 0, 0, 0)
            if (attrs != null) {
                val a = context
                    .obtainStyledAttributes(attrs, R.styleable.ImageFilterView)
                val count = a.indexCount
                for (i in 0 until count) {
                    val attr = a.getIndex(i)
                    if (attr == R.styleable.ImageFilterView_round) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            round = a.getDimension(attr, 0f)
                        }
                    } else if (attr == R.styleable.ImageFilterView_roundPercent) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            roundPercent = a.getFloat(attr, 0f)
                        }
                    }
                }
                a.recycle()
            }
        }
        /**
         * Get the fractional corner radius of curvature.
         *
         * @return Fractional radius of curvature with respect to smallest size
         */
        /**
         * Set the corner radius of curvature  as a fraction of the smaller side.
         * For squares 1 will result in a circle
         *
         * @param round the radius of curvature as a fraction of the smaller width
         */
        @set:RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        var roundPercent: Float
            get() = mRoundPercent
            set(round) {
                val change = mRoundPercent != round
                mRoundPercent = round
                if (mRoundPercent != 0.0f) {
                    if (mPath == null) {
                        mPath = Path()
                    }
                    if (mRect == null) {
                        mRect = RectF()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (mViewOutlineProvider == null) {
                            mViewOutlineProvider = object : ViewOutlineProvider() {
                                override fun getOutline(view: View?, outline: Outline) {
                                    val w = width
                                    val h = height
                                    val r = Math.min(w, h) * mRoundPercent / 2
                                    outline.setRoundRect(0, 0, w, h, r)
                                }
                            }
                            outlineProvider = mViewOutlineProvider
                        }
                        clipToOutline = true
                    }
                    val w = width
                    val h = height
                    val r = Math.min(w, h) * mRoundPercent / 2
                    mRect!![0f, 0f, w.toFloat()] = h.toFloat()
                    mPath!!.reset()
                    mPath!!.addRoundRect(mRect!!, r, r, Path.Direction.CW)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        clipToOutline = false
                    }
                }
                if (change) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        invalidateOutline()
                    }
                }
            }
        /**
         * Get the corner radius of curvature NaN = RoundPercent in effect.
         *
         * @return Radius of curvature
         */// force eval of roundPercent
        /**
         * Set the corner radius of curvature
         *
         * @param round the radius of curvature  NaN = default meaning roundPercent in effect
         */
        var round: Float = 0f
            get() = mRound

        override fun draw(canvas: Canvas) {
            var clip = false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                if (mRound != 0.0f && mPath != null) {
                    clip = true
                    canvas.save()
                    canvas.clipPath(mPath!!)
                }
            }
            super.draw(canvas)
            if (clip) {
                canvas.restore()
            }

        }


}
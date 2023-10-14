package unipi.sam.emusic.utils

import android.content.res.Resources
import android.util.TypedValue

val Number.toPx get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics)
abstract class MathUtil {

    companion object {
        fun lerp(a: Float, b: Float, f: Float): Float {
            return a + f * (b - a)
        }
    }
}

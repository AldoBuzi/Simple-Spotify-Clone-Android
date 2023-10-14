package unipi.sam.emusic.ux_classes.genre

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import kotlinx.coroutines.async
import kotlinx.coroutines.newSingleThreadContext
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.ux_classes.BaseContainerLayout
import unipi.sam.emusic.ux_classes.ContainerLayout
import unipi.sam.emusic.ux_classes.Widget
import unipi.sam.emusic.ux_classes.track_view.TrackViewAdapter

class GenresView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : BaseContainerLayout(context,attrs), ContainerLayout {

    lateinit var adapter: GenreAdapter

    override fun onFinishInflate() {
        //hasRecyclerView = false
        super.onFinishInflate()
        recyclerView.layoutManager = GridLayoutManager(
            context,
            2,
            LinearLayoutManager.VERTICAL,
            false
        )

    }


    override fun setModels(value: Collection<ModelViewType>) {
        adapter = GenreAdapter(value,activity!!)
        recyclerView.adapter = adapter

        val mScrollTouchListener: RecyclerView.OnItemTouchListener =
            object : RecyclerView.OnItemTouchListener {
                private var downXpos = 0f
                private var downYpos = 0f
                private var touchcaptured = false
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    when (e.getAction()) {
                        MotionEvent.ACTION_DOWN -> {
                            downXpos = e.getX()
                            downYpos = e.getY()
                            touchcaptured = false
                        }

                        MotionEvent.ACTION_UP -> requestDisallowInterceptTouchEvent(false)
                        MotionEvent.ACTION_MOVE -> {
                            val xdisplacement: Float = Math.abs(e.getX() - downXpos)
                            val ydisplacement: Float = Math.abs(e.getY() - downYpos)
                            if (!touchcaptured && xdisplacement > ydisplacement && xdisplacement > 10) {
                                requestDisallowInterceptTouchEvent(false)
                                touchcaptured = true
                            }
                            else requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            }
        recyclerView.addOnItemTouchListener(mScrollTouchListener)
    }

}
package unipi.sam.emusic.ux_classes

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType

abstract class BaseContainerLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ContainerLayout {

    open lateinit var recyclerView: RecyclerView
    override fun onFinishInflate() {
        super.onFinishInflate()
        if(hasRecyclerView) {
            recyclerView = findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(
                context,
                RecyclerView.HORIZONTAL,
                false
            )
        }
    }

    override var widget: Widget? = null
        set(value) {
            field = value
            items = value!!.items
        }
    protected open var items: Collection<ModelViewType>? = null
        set(value) {
            field = value
            setModels(value!!)
        }

    //used as callback for passing collection
    abstract fun setModels(value: Collection<ModelViewType>)
    override var activity: MainActivity? = null

    var hasRecyclerView = true
}
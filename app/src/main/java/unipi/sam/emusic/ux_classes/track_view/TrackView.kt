package unipi.sam.emusic.ux_classes.track_view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.ux_classes.ViewContentLayout
import java.lang.Exception

class TrackView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs),
    ViewContentLayout {



    var localItem : Pair<MutableList<TrackModel>,Int>? = null

    lateinit var coverImage: ImageView
    lateinit var title: TextView
    lateinit var subTitle: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        coverImage = findViewById(R.id.track_view_contentImage)
        title = findViewById(R.id.track_view_titleText)
        subTitle = findViewById(R.id.track_view_AlbumText)
    }


    override fun showLoadedContent(item: ModelViewType) {
        if(item !is TrackModel) return
        try{
            ImageUtils.loadImageFromUrl(item.album!!.coverImage!!,coverImage,coverImage)
        }
        catch (e:Exception) { e.printStackTrace() }
        title.text = item.title
        subTitle.text = "${item.type} • ${item.artist?.name}"
    }

    override fun setListener(activity: MainActivity) {
        if(localItem != null)
            activity.presentPlayer(localItem!!.first, localItem!!.second)
    }
}
package unipi.sam.emusic.ux_classes.podcast

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import unipi.sam.emusic.R
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PodcastModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout

class PodcastView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ViewContentLayout {


    lateinit var contentImage: ImageView
    lateinit var title: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        contentImage = findViewById(R.id.podcast_contentImage)
        title = findViewById(R.id.podcast_mainTitle)
    }

    override fun showLoadedContent(item: ModelViewType) {
        if (item !is PodcastModel) return

        if(item.picture != null)
            ImageUtils.loadImageFromUrl(item.picture,contentImage,contentImage)
        title.text = item.title
    }

}
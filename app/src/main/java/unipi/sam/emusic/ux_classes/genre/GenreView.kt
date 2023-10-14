package unipi.sam.emusic.ux_classes.genre

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.models.GenreModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout

class GenreView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ViewContentLayout {


    private var localItem: ModelViewType? = null

    private lateinit var genreImage: ImageView
    private lateinit var genreTitle : TextView
    private lateinit var genreViewContainer: ConstraintLayout
    override fun onFinishInflate() {
        super.onFinishInflate()
        genreImage = findViewById(R.id.genre_image)
        genreTitle = findViewById(R.id.genre_titleText)
        genreViewContainer = findViewById(R.id.genre_viewContainer)
    }


    override fun showLoadedContent(item: ModelViewType) {
        if (item !is GenreModel) return

        localItem = item

        if(item.picture != null)
            ImageUtils
                .loadImageFromUrl(item.picture, genreImage) {
                    handleColorChange()
                }
        genreTitle.text = item.name
    }


    fun handleColorChange(){
        Palette.from(genreImage.drawable.toBitmap()).generate(Palette.PaletteAsyncListener() {
            var value = -1
            if(it != null && it.mutedSwatch?.rgb != null)  value =  it.mutedSwatch!!.rgb
            else if(it != null && it.vibrantSwatch?.rgb != null) value = it.vibrantSwatch!!.rgb
            if(value!=-1) genreViewContainer.setBackgroundColor(value)
        })
    }

    override fun setListener(activity: MainActivity) {
        if(localItem == null) return
        activity.presentGenreExplorerPage(localItem!!)
    }
}
package unipi.sam.emusic.ux_classes.custom_playlist

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputCustom
import unipi.sam.emusic.R
import unipi.sam.emusic.commons.SnackBarProvider
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout

class CustomArtistView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ViewContentLayout, CustomPlaylistViews {


    override lateinit var activity: MainActivity


    private var localItem : PlaylistModel? = null

    private lateinit var artistImage: ImageView
    private lateinit var title: TextView
    private lateinit var optionsBtn : ImageButton

    override fun onFinishInflate() {
        super.onFinishInflate()
        artistImage = findViewById(R.id.artist_mainImage)
        title = findViewById(R.id.artist_titleText)
        optionsBtn = findViewById(R.id.custom_ArtistOptionsBtn)

    }

    override fun showLoadedContent(item: ModelViewType) {
        if (item !is PlaylistModel) return
        localItem = item
        title.text = item.title
        if (item.picture != null)
            ImageUtils.loadImageFromUrl(item.picture, artistImage,artistImage)
        val localActivity = activity
        optionsBtn.setOnClickListener {
            showOptionSheet(localActivity , item)
        }
    }

    override fun setListener(activity: MainActivity) {
        if(localItem == null) return
        activity.presentArtistDetailPage(ArtistModel.parseRoom(localItem!!))
    }

    private fun showOptionSheet(localActivity: MainActivity, item: PlaylistModel){
        val sheet = InputSheet()
        sheet.show(activity){
            with(InputCustom(){
                view(R.layout.playlist_options_dialog) {
                    it.findViewById<LinearLayout>(R.id.deleteOptionContainer).setOnClickListener {
                        localActivity.deletePlaylist(item.databaseId) {
                            SnackBarProvider.getSnackBar(localActivity,"Artista eliminato con successo")
                                .show()
                        }
                        this@show.dismiss()

                    }
                    val artistContainer = it.findViewById<LinearLayout>(R.id.seeArtistOptionContainer)
                    artistContainer.visibility = GONE
                }
            })
            displayButtons(false)
            displayCloseButton(false)
        }
    }


}
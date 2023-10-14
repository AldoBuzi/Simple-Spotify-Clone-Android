package unipi.sam.emusic.ux_classes.custom_playlist

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputCustom
import unipi.sam.emusic.R
import unipi.sam.emusic.commons.SnackBarProvider
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.ViewContentLayout


class CustomPlaylistView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ViewContentLayout, CustomPlaylistViews {


    override lateinit var activity: MainActivity

    private var localItem : ModelViewType? = null

    private lateinit var contentImage: ImageView
    private lateinit var title: TextView
    private lateinit var subTitle: TextView
    private lateinit var optionsBtn: ImageButton
    override fun onFinishInflate() {
        super.onFinishInflate()
        contentImage = findViewById(R.id.custom_playlist_mainImage)
        title = findViewById(R.id.custom_playlist_titleText)
        subTitle = findViewById(R.id.custom_playlist_subTitleText)
        optionsBtn = findViewById(R.id.custom_playlistOptionsBtn)
    }

    override fun showLoadedContent(item: ModelViewType) {

        if (item !is PlaylistModel) return

        localItem = item
        if(item.picture != null)
            ImageUtils.loadImageFromUrl(item.picture,contentImage,contentImage)
        title.text = item.title
        subTitle.text = item.type
        val localActivity = activity
        optionsBtn.setOnClickListener {
            showOptionSheet(localActivity , item)
        }
    }

    override fun setListener(activity: MainActivity) {
        if(localItem == null) return
        if(localItem !is PlaylistModel) return
        activity.presentCustomPlaylist(localItem as PlaylistModel)
    }


    private fun showOptionSheet(localActivity: MainActivity, item: PlaylistModel){
        val sheet = InputSheet()
        sheet.show(activity){
            with(InputCustom(){
                view(R.layout.playlist_options_dialog) {
                    it.findViewById<LinearLayout>(R.id.deleteOptionContainer).setOnClickListener {
                        localActivity.deletePlaylist(item.databaseId) {
                            SnackBarProvider.getSnackBar(localActivity,"Playlist eliminata con successo")
                                .show()
                        }
                        this@show.dismiss()

                    }
                    val artistContainer = it.findViewById<LinearLayout>(R.id.seeArtistOptionContainer)
                    if(item.id!! <=0) artistContainer.visibility = View.GONE
                    else artistContainer.setOnClickListener {
                        localActivity.presentArtistDetailPage(item)
                    }
                }
            })
            displayButtons(false)
            displayCloseButton(false)
        }
    }
}
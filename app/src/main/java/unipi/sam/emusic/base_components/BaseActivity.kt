package unipi.sam.emusic.base_components

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import unipi.sam.emusic.R
import unipi.sam.emusic.components.player.PlayerFragment
import unipi.sam.emusic.components.player.PlayerViewModel
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.utils.toPx


open class BaseActivity : AppCompatActivity() {

    lateinit var bottomNavigationView : BottomNavigationView
    lateinit var navController: NavController
    var appBarLayout : AppBarLayout? = null


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    fun presentPlayer(item: MutableList<TrackModel>, seekIndex: Int){
        if (!PlayerViewModel.isInitialized) {
            hideActionBar()
            hideBottomNav()
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = PlayerFragment.newInstance()
            val bundle = Bundle()
            bundle.putParcelableArray("item",item.toTypedArray())
            PlayerViewModel.seekIndex = seekIndex
            fragment.arguments = bundle
            transaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down
            )
            transaction.add(
                R.id.player_container,
                fragment
            )
            transaction.commit()
        }
        else {
            PlayerViewModel.item = item
            PlayerViewModel.seekIndex = seekIndex
            PlayerViewModel.isReloadRequired.value = true
        }
    }
    fun showActionBar() {
        appBarLayout?.removeOnOffsetChangedListener(firstListener)
        appBarLayout?.visibility = View.VISIBLE
        appBarLayout?.setExpanded(true)
        appBarLayout?.addOnOffsetChangedListener(secondListener)

    }
    open fun hideActionBar() {
        appBarLayout?.setExpanded(false)
    }
    fun setActionBarGone(){
        appBarLayout?.removeOnOffsetChangedListener(secondListener)
        appBarLayout?.setExpanded(false)
        appBarLayout?.addOnOffsetChangedListener(firstListener)
    }

    override fun onBackPressed() {
        if(PlayerViewModel.isSmall.value == true) PlayerViewModel.grabPosition.value = true
        if(PlayerViewModel.isInitialized && PlayerViewModel.isSmall.value == false){
            PlayerViewModel.requestMiniPlayer.value = true
            return
        }
        super.onBackPressed()
    }
    fun hideBottomNav(){
        bottomNavigationView.clearAnimation()
        bottomNavigationView.animate().translationY(80f.toPx).setDuration(150)
    }
    fun showBottomNav(){
        bottomNavigationView.clearAnimation()
        bottomNavigationView.animate().translationY(0f.toPx).setDuration(150)
    }

    private var firstListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
        {
            //  Collapsed
            appBarLayout.visibility = View.GONE
        }
        else {/*Expanded*/ }
    }

    val secondListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0) {/*  Collapsed */}
        else
        {
            //Expanded
            appBarLayout.visibility = View.VISIBLE
            appBarLayout.removeOnOffsetChangedListener(null)
        }
    }


}
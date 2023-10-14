package unipi.sam.emusic.main_module

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SimpleAdapter.ViewBinder
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.search.SearchView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseActivity
import unipi.sam.emusic.databinding.ActivityMainBinding
import unipi.sam.emusic.main_navigation.profile.ProfileViewModel
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.ModelViewType
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.utils.toPx
import unipi.sam.emusic.ux_classes.LayoutAdapter
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private val STORAGE_PERMISSION_CODE = 101
    private val NOTIFICATION_PERMISSION_CODE = 100
    @Inject lateinit var database : AppDatabase
    val viewModel: MainViewModel by viewModels()
    lateinit var sharedPref : SharedPreferences
    lateinit var searchView: SearchView
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setupBottomNavigation()
        appBarLayout = binding.appBarLayout
        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val isInitialised = sharedPref.getBoolean(getString(R.string.database_init),false)
        if(!isInitialised)
            lifecycleScope.launch (Dispatchers.IO) {
                if(database.playlistDao().init() == 1L){
                    with (sharedPref.edit()) {
                        putBoolean(getString(R.string.database_init), true)
                        apply()
                    }
                }
            }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE);
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){
                getNotificationPermission();
        }

        setSearch()
    }
    fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this, arrayOf<String>(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun setupBottomNavigation(){
        bottomNavigationView    = binding.bottomNavigation
        val navHostFragment     =  supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController       = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.artistDetailFragment, R.id.albumDetailFragment, R.id.playlistDetailFragment -> {
                    setActionBarGone()
                    searchView.hide()
                }
                //R.id.discoveryFragment -> hideActionBar()
                R.id.homeFragment, R.id.discoveryFragment, R.id.profileFragment -> showActionBar()
                else -> showActionBar()
            }

        }
        bottomNavigationView.setOnItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
            navController.popBackStack(it.itemId, inclusive = false)
            true
        }
    }

    override fun onBackPressed() {
        if(isSearchOpen){
            searchView.hide()
            return
        }
        super.onBackPressed()
        resetContainer()
    }
    private fun resetContainer(){
        binding.playerContainer.y = 0f
    }

    fun deletePlaylist(playlistID: Int, callback : (() -> Unit)? = null){
        lifecycleScope.async (Dispatchers.IO) {
            if ( database.playlistDao().deleteByDatabaseId(playlistID) == 1 ){
                callback?.invoke()
                ProfileViewModel.needRefresh.postValue(true)
            }

        }
    }
    fun presentCustomPlaylist(playlist: PlaylistModel){
        navController.navigate(R.id.playlistDetailFragment, Bundle().apply { putParcelable("playlist",playlist) })

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun setSearch(){
        searchView = binding.mainSearchView
        val suggestRecycler = binding.suggestRecycler
        val suggestRecyclerContainer = binding.suggestRecyclerContainer
        val suggestLoadingSpinner = binding.suggestLoadingSpinner
        searchView.editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.handleSearchTextChange(p0?.toString())

            }
            override fun afterTextChanged(p0: Editable?) {}
        })
        var searchAdapter : LayoutAdapter? = null
        viewModel.searchWidgets.observe(this, Observer {
            suggestRecycler.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
            )
            if(searchAdapter == null){
                searchAdapter = LayoutAdapter(it, this)
                suggestRecycler.adapter = searchAdapter
            }
            searchAdapter?.setNewItems(it)
            searchAdapter?.notifyDataSetChanged()

        })
        searchView.addTransitionListener { _, _, newState ->
            isSearchOpen = true
            if(newState == SearchView.TransitionState.HIDDEN) {
                isSearchOpen = false
                viewModel.handleSearchTextChange(null)
            }
        }
        viewModel.isSuggestionVisible.observe(this, Observer {
            suggestRecyclerContainer.visibility = if(it) View.VISIBLE else View.GONE
            suggestLoadingSpinner.visibility = if(!it) View.VISIBLE else View.GONE
            // binding.recyclerView.visibility = if(!it) View.VISIBLE else View.GONE
        })
        viewModel.isSearching.observe(this, Observer {
            //recyclerView.visibility = if(it) View.GONE else View.VISIBLE
            if(!it) suggestRecyclerContainer.visibility = View.GONE
            if(!it) suggestLoadingSpinner.visibility =  View.GONE
        })

    }
    var lastImage : ImageView? = null
    fun withImageAnimation(imageView: ImageView) : MainActivity{
        lastImage = imageView
        return this
    }
    fun presentArtistDetailPage(item: ModelViewType){
        val extras = if(lastImage!= null) FragmentNavigatorExtras(
            lastImage!! to (item as ArtistModel).picture!!
        ) else null
        navController.navigate(R.id.artistDetailFragment, bundleOf("item" to item),null,extras)
    }
    fun presentAlbumDetailPage(item: ModelViewType){
        val extras = if(lastImage!= null) FragmentNavigatorExtras(
            lastImage!! to (item as AlbumModel).cover!!
        ) else null
        navController.navigate(R.id.albumDetailFragment, bundleOf("item" to item),null,extras)
    }
    fun presentGenreExplorerPage(item: ModelViewType){
        navController.navigate(R.id.genreEXplorerFragment, bundleOf("item" to item))
    }

    companion object{
        var isSearchOpen = false
    }


}
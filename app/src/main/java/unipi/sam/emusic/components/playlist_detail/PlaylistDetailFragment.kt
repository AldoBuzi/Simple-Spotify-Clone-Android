package unipi.sam.emusic.components.playlist_detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.NO_ID
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.commons.SnackBarProvider
import unipi.sam.emusic.databinding.FragmentPlaylistDetailBinding
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.PlaylistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.models.User
import unipi.sam.emusic.utils.MathUtil
import unipi.sam.emusic.utils.toPx
import unipi.sam.emusic.ux_classes.Widget
import unipi.sam.emusic.ux_classes.track_list.TrackListAdapter
import kotlin.math.absoluteValue

@AndroidEntryPoint
class PlaylistDetailFragment : BaseFragment<FragmentPlaylistDetailBinding>(R.layout.fragment_playlist_detail) {


    val viewModel : PlaylistDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // get args
            //if(it.get("playlistID") == null) return
            viewModel.item = it.get("playlist") as PlaylistModel

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        if(!viewModel.hasDataLoaded) {
            lifecycleScope.launch(Dispatchers.Unconfined) {
                delay(200)
                withContext(Dispatchers.Main) {
                    viewModel.onCreate()
                }

                viewModel.loadTracks()
            }
        }
        viewModel.widgets.observe(viewLifecycleOwner, Observer {
            showLoadedData(it)
        })
        setupUI()
        setOffsetListener()
        return binding.root
    }

    override fun onDestroy() {
        viewModel.onDestroy()
        super.onDestroy()
    }
    companion object {

        @JvmStatic
        fun newInstance() =
            PlaylistDetailFragment().apply {
                arguments = Bundle().apply {
                    //put args
                }
            }
    }

    override fun showLoadedData(data: Collection<Widget>) {
        super.showLoadedData(data)
        if(data.isEmpty() || data.elementAt(0).items.isEmpty()){
            binding.stateful.getChildAt(0).visibility = View.VISIBLE
            binding.stateful.getChildAt(1).visibility = View.GONE
            return
        }
        binding.recyclerView.layoutManager = object : LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        ){
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                binding.stateful.visibility = View.GONE
            }
        }
        if(binding.recyclerView.adapter == null)
            binding.recyclerView.adapter = TrackListAdapter(data.elementAt(0).items.apply { add(TrackModel(id = NO_ID)) },requireActivity() as MainActivity)
        else adapter = null
    }


    private fun setupUI(){
        PlaylistDetailViewModel.needRefresh.observe(viewLifecycleOwner, Observer {
            if(it)
                lifecycleScope.launch  (Dispatchers.Unconfined) {
                    viewModel.loadTracks()
                }
        })
        binding.backBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.playlistDetailPlayShuffleBtn.setOnClickListener {
            val list = viewModel.handlePlayButton()
            (activity as? MainActivity)?.presentPlayer(list,0)
        }
        viewModel.title.observe(viewLifecycleOwner, Observer {
            binding.playlistDetailTitleText.text = it
        })
        binding.playlistDetailAddBtn.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Main){
                viewModel.handleAddBtn()
            }

        }
        viewModel.isPlaylistSaved.observe(viewLifecycleOwner, Observer {
            binding.playlistDetailAddBtn.setImageResource(if(it) R.drawable.tick else R.drawable.add_small)
        })
        viewModel.showSnackBar.observe(viewLifecycleOwner, Observer {
            if(it) SnackBarProvider.getSnackBar(requireActivity() as MainActivity, "Playlist salvata").show()
            else SnackBarProvider.getSnackBar(requireActivity() as MainActivity, "Playlist Eliminata").show()
        })
    }

    private fun setOffsetListener(){
        viewModel.btnStartingPosition = binding.playlistDetailPlayShuffleBtn.y
        binding.appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            override fun onOffsetChanged(appBarLayout: AppBarLayout?, offset: Int) {
                val percentage = offset.absoluteValue.toFloat() / appBarLayout!!.totalScrollRange.toFloat()

                val alphaAnimator = MathUtil.lerp(0f, 1f ,percentage)
                binding.appBarSecondContainer.post {
                    binding.appBarSecondContainer.alpha = alphaAnimator
                }
                val titleMovement = MathUtil.lerp(85.toPx,145.toPx, percentage)
                binding.playlistDetailTitleText.post {
                    binding.playlistDetailTitleText.y = titleMovement
                }
                val fasterAlphaAnimator = MathUtil.lerp(0f, 1f ,percentage * 3)
                binding.playlistDetailShuffleBtn.post {
                    binding.playlistDetailShuffleBtn.alpha  = 1f - fasterAlphaAnimator
                }
                binding.playlistDetailAddBtn.post {
                    binding.playlistDetailAddBtn.alpha = 1f - fasterAlphaAnimator
                }
                val buttonMovement = MathUtil.lerp(viewModel.btnStartingPosition!!,30.toPx, percentage)
                binding.playlistDetailPlayShuffleBtn.post {
                    binding.playlistDetailPlayShuffleBtn.translationY = buttonMovement
                }
            }
        })
    }
}
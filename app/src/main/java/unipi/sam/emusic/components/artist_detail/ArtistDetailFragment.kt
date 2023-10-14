package unipi.sam.emusic.components.artist_detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.commons.FragmentInterface
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.commons.SnackBarProvider
import unipi.sam.emusic.databinding.FragmentArtistDetailBinding
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.utils.toPx
import unipi.sam.emusic.ux_classes.LayoutAdapter
import unipi.sam.emusic.ux_classes.Widget

@AndroidEntryPoint
class ArtistDetailFragment : BaseFragment<FragmentArtistDetailBinding>(R.layout.fragment_artist_detail) {


    private val viewModel : ArtistDetailViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.get("item") != null) {
                viewModel.item = it.get("item") as ArtistModel
            }
        }
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = this
        binding.stateful.showLoading()
        ViewCompat.setTransitionName(binding.artistMainImageTransition, viewModel.item!!.picture!!)
        val dataObserver = Observer<Collection<Widget>> { data ->
            showLoadedData(data)
        }

        if(!viewModel.isDataLoaded)
            lifecycleScope.launch  (newSingleThreadContext("artist_detail_fetch")) {
                delay(200)
                launch {  viewModel.loadTracks() }
                launch { viewModel.loadPlaylists() }
                launch { viewModel.loadRelated() }
                launch { viewModel.loadAlbums() }
            }
        viewModel.widgets.observe(viewLifecycleOwner,dataObserver)
        setupUI()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //ViewCompat.setTransitionName(binding.artistMainImageTransition, "item_image")
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            ArtistDetailFragment().apply {
                arguments = Bundle().apply {
                    //put args
                }
            }
    }

    override fun showLoadedData(data: Collection<Widget>){
        super.showLoadedData(data)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        binding.stateful.showContent()

    }
    private fun setupUI(){
        viewModel.picture.observe(viewLifecycleOwner, Observer {
            ImageUtils.loadForAnimation(binding.artistMainImageTransition,it)
        })
        viewModel.artistName.observe(viewLifecycleOwner, Observer {
            binding.artistNameText.text = it
        })
        binding.backBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.artistPlayShuffleBtn.setOnClickListener {
            val list = viewModel.handlePlayButton()
            (activity as? MainActivity)?.presentPlayer(list,0)
        }
        binding.artistDetailAddBtn.setOnClickListener {
            lifecycleScope.launch (Dispatchers.Main){
                viewModel.handleAddBtn()
            }

        }
        viewModel.isArtistSaved.observe(viewLifecycleOwner, Observer {
            binding.artistDetailAddBtn.setImageResource(if(it) R.drawable.tick else R.drawable.add_small)
        })
        viewModel.showSnackBar.observe(viewLifecycleOwner, Observer {
            if(it) SnackBarProvider.getSnackBar(requireActivity() as MainActivity, "Playlist salvata").show()
            else SnackBarProvider.getSnackBar(requireActivity() as MainActivity, "Playlist Eliminata").show()
        })
    }
}
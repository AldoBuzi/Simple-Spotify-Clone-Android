package unipi.sam.emusic.components.album_detail

import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.databinding.FragmentAlbumDetailBinding
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.AlbumModel
import unipi.sam.emusic.models.ArtistModel
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.Widget

@AndroidEntryPoint
class AlbumDetailFragment : BaseFragment<FragmentAlbumDetailBinding>(R.layout.fragment_album_detail) {


    private val viewModel : AlbumDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.get("item") != null) {
                viewModel.item = it.get("item") as AlbumModel
            }
        }
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        ViewCompat.setTransitionName(binding.artistMainImageTransition, viewModel.item!!.cover!!)
        val dataObserver = Observer<Collection<Widget>> { data ->
            showLoadedData(data)
        }
        if(!viewModel.hasDataLoaded)
            lifecycleScope.launch  (newSingleThreadContext("album_detail_fetch")) {
                    delay(200)
                    viewModel.loadTrackList()
                }
        viewModel.widgets.observe(viewLifecycleOwner,dataObserver)
        setupUI()
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AlbumDetailFragment().apply {
                arguments = Bundle().apply {
                    //put args
                }
            }
    }

    override fun showLoadedData(data: Collection<Widget>) {
        super.showLoadedData(data)
        binding.recyclerView.layoutManager = object : LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        ){
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                binding.stateful.visibility = View.GONE
            }
        }
        binding.recyclerView.setHasFixedSize(true)
        if (binding.recyclerView.adapter == null)
            binding.recyclerView.adapter = adapter
        else adapter = null


    }

    private fun setupUI(){
        viewModel.picture.observe(viewLifecycleOwner, Observer {
            ImageUtils.loadForAnimation(binding.artistMainImageTransition,it){
                    viewModel.handleColorChange(it!!.toBitmap()){
                        binding.albumDetailContainer.setBackgroundDrawable(it)
                    }
                }
        })
        viewModel.title.observe(viewLifecycleOwner, Observer {
            binding.albumNameText.text = it
        })
        binding.backBtn.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.albumPlayShuffleBtn.setOnClickListener {
            val list = viewModel.handlePlayButton()
            (activity as? MainActivity)?.presentPlayer(list,0)
        }
    }
}
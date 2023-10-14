package unipi.sam.emusic.components.genre_explorer

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.custom.SpacingItemDecorator
import unipi.sam.emusic.databinding.FragmentGenreExplorerBinding
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.GenreModel
import unipi.sam.emusic.utils.toPx
import unipi.sam.emusic.ux_classes.Widget
import unipi.sam.emusic.ux_classes.artist.ArtistAdapter
import unipi.sam.emusic.ux_classes.genre.GenreAdapter

@AndroidEntryPoint
class GenreExplorerFragment : BaseFragment<FragmentGenreExplorerBinding>(R.layout.fragment_genre_explorer) {


    val viewModel : GenreExplorerViewModel by viewModels ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.get("item") == null) return
            viewModel.item = it.get("item") as GenreModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        if(!viewModel.isDataLoaded)
            lifecycleScope.launch (newSingleThreadContext("genre_explorer_fetch")) {
                viewModel.loadGenre()
            }
        val dataObserver = Observer<Collection<Widget>> { data ->
            showLoadedData(data)
        }
        viewModel.widgets.observe(viewLifecycleOwner,dataObserver)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            GenreExplorerFragment().apply {
                arguments = Bundle().apply {
                    //put args
                }
            }
    }

    override fun showLoadedData(data: Collection<Widget>) {
        super.showLoadedData(data)
        binding.recyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            GridLayoutManager.VERTICAL  ,
            false
        )
        binding.recyclerView.setHasFixedSize(true)
        adapter = null
        if(binding.recyclerView.adapter == null) {
            binding.recyclerView.adapter = ArtistAdapter(data.elementAt(0).items, activity as MainActivity)
            binding.recyclerView.addItemDecoration(SpacingItemDecorator(5.toPx.toInt()))
        }
    }
}
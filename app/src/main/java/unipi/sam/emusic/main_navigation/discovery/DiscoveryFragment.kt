package unipi.sam.emusic.main_navigation.discovery


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.search.SearchBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.databinding.FragmentDiscoveryBinding
import unipi.sam.emusic.ux_classes.LayoutAdapter
import unipi.sam.emusic.ux_classes.Widget


@AndroidEntryPoint
class DiscoveryFragment : BaseFragment<FragmentDiscoveryBinding>(R.layout.fragment_discovery) {


     val viewModel: DiscoverViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
           //get args
        }
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        onToolbarCreation(R.layout.discover_toolbar)
        binding.lifecycleOwner = viewLifecycleOwner
        val dataObserver = Observer<Collection<Widget>> { data ->
            lifecycleScope.launch (Dispatchers.Main){
                showLoadedData(data)
            }
        }
        viewModel.widgets.observe(viewLifecycleOwner,dataObserver)
        if(!viewModel.isDataLoaded)
            lifecycleScope.launch (newSingleThreadContext("search_fetch")) {
                viewModel.loadGenres()
            }
        else binding.loadingView.visibility = View.GONE
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            DiscoveryFragment().apply {
                arguments = Bundle().apply {
                    //put args
                }
            }
        var localAdapter: LayoutAdapter? = null
    }


    override fun showLoadedData(data: Collection<Widget>) {
        super.showLoadedData(data)
        if(localAdapter == null) localAdapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = object: LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false,

        ){
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                binding.loadingView.visibility = View.GONE
            }

        }
        if(!viewModel.isDataLoaded)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.recyclerView.adapter = localAdapter
            },200)
        else lifecycleScope.launch (Dispatchers.Main) {
            binding.recyclerView.adapter = localAdapter
        }


    }

}
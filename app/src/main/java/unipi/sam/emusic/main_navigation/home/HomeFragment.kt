package unipi.sam.emusic.main_navigation.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout

import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.databinding.FragmentHomeBinding
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject
@AndroidEntryPoint
@FragmentScoped
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

   @Inject lateinit var database : AppDatabase

   val viewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //get extras
        }


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        onToolbarCreation(R.layout.home_toolbar)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.stateful.showLoading()
        val dataObserver = Observer<Collection<Widget>> { data ->
            showLoadedData(data)
        }
        viewModel.widgets.observe(viewLifecycleOwner,dataObserver)
        if(!viewModel.isDataLoaded)
            lifecycleScope.launch (newSingleThreadContext("home_fetch")) {
                viewModel.loadData()
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    //put extras
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
        if(binding.recyclerView.adapter == null)
            binding.recyclerView.adapter = adapter
        else adapter = null
        binding.stateful.showContent()
    }
}
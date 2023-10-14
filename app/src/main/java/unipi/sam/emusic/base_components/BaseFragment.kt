package unipi.sam.emusic.base_components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.search.SearchView
import com.google.gson.JsonObject
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.commons.FragmentInterface
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.LayoutAdapter
import unipi.sam.emusic.ux_classes.Widget

abstract class BaseFragment<VB : ViewBinding>  constructor(val layout: Int): Fragment(), FragmentInterface {
     val TAG: String = "BaseFragment"

     lateinit var binding: VB


     override var adapter : LayoutAdapter? = null

     override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
     ): View {
          if(!this::binding.isInitialized)
               binding = DataBindingUtil.inflate(inflater,layout,container, false)
          return binding.root
     }

     override fun showLoadedData(data: Collection<Widget>) {
          val data  = mutableListOf<Widget>(*data.toTypedArray())
          data.add(Widget(JsonObject(),LayoutType.EMPTY_VIEW,""))
          adapter = LayoutAdapter(data, activity as MainActivity)
     }

     open fun onToolbarCreation(toolbarLayout: Int): ViewGroup{
          val toolbar = layoutInflater.inflate(toolbarLayout,binding.root as ViewGroup,false)
          (activity as? MainActivity)?.binding?.toolbar?.removeAllViews()
          (activity as? MainActivity)?.binding?.toolbar?.addView(toolbar)
          if(toolbar.findViewById<View>(R.id.discovery_searchBar) != null)
               (activity as? MainActivity)?.findViewById<SearchView>(R.id.main_searchView)?.setupWithSearchBar(toolbar.findViewById(R.id.discovery_searchBar))
          if(toolbar.findViewById<View>(R.id.toolbarSearch) != null)
               (activity as? MainActivity)?.findViewById<SearchView>(R.id.main_searchView)?.setupWithSearchBar(toolbar.findViewById(R.id.toolbarSearch))
          return toolbar as ViewGroup

     }


}
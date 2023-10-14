package unipi.sam.emusic.main_navigation.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.textfield.TextInputEditText
import com.maxkeppeler.sheets.core.Sheet
import com.maxkeppeler.sheets.input.InputSheet
import com.maxkeppeler.sheets.input.type.InputCustom
import com.maxkeppeler.sheets.input.type.InputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.commons.CustomDialog
import unipi.sam.emusic.databinding.FragmentProfileBinding
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.models.User
import unipi.sam.emusic.room.AppDatabase
import unipi.sam.emusic.utils.toPx
import unipi.sam.emusic.ux_classes.Widget
import unipi.sam.emusic.ux_classes.custom_playlist.CustomPlaylistAdapter
import java.lang.Exception
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    val viewModel : ProfileViewModel by viewModels()

    @Inject lateinit var database: AppDatabase
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
        val toolbar = onToolbarCreation(R.layout.profile_toolbar)
        handleAddBtn(toolbar.findViewById(R.id.toolbar_addBtn))
        binding.lifecycleOwner = viewLifecycleOwner
        lifecycleScope.launch (Dispatchers.Unconfined) {
            viewModel.getPlaylists()
        }
        viewModel.widgets.observe(viewLifecycleOwner, Observer {
            showLoadedData(it)
        })
        setupUI()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    //put Args
                }
            }
    }

    private fun setupUI(){
        ProfileViewModel.needRefresh.observe(viewLifecycleOwner, Observer {
            if(it)
                lifecycleScope.launch (Dispatchers.Unconfined) {
                    viewModel.getPlaylists()
                }
        })
    }
    override fun showLoadedData(data: Collection<Widget>) {
        super.showLoadedData(data)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.recyclerView.adapter = CustomPlaylistAdapter(data.elementAt(0).items,requireActivity() as MainActivity)

    }
    private fun handleAddBtn(btn: ImageButton?){
        btn?.setOnClickListener {
            val sheet = InputSheet()
            sheet.show(requireActivity() as AppCompatActivity){
                with(InputCustom(){
                    view(R.layout.custom_dialog_layout) {
                        val textInput = it.findViewById<TextInputEditText>(R.id.customDialogTextInput)
                        it.findViewById<Button>(R.id.customDialogBtn).setOnClickListener {
                            lifecycleScope.launch (Dispatchers.IO)  {
                                viewModel.createPlaylist(textInput.text.toString())
                            }
                            this@show.dismiss()
                        }
                    }
                })
                customLayoutHeight(binding.root.height - 200.toPx.toInt())
                displayButtons(false)
                displayCloseButton(false)
            }

        }

    }
}
package unipi.sam.emusic.custom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.gson.Gson
import com.gturedi.views.StatefulLayout
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import unipi.sam.emusic.R
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.room.AppDatabase
//import unipi.sam.emusic.room.Playlist
import unipi.sam.emusic.room.PlaylistAndTrack
//import unipi.sam.emusic.room.Track
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistBottomSheet(val item : TrackModel) : BottomSheetDialogFragment() {
    @Inject lateinit var database: AppDatabase
    lateinit var root : View
    override fun onCreateView(
        inflater: LayoutInflater,
         container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(
            R.layout.playlist_bottom_sheet, container,
            false
        )
        lifecycleScope.launch (Dispatchers.Main) {
            showLoadedData(database.playlistDao().getLocalPlaylist())
        }
        return  root
    }

    fun showLoadedData(data: List<PlaylistAndTrack>){
        val stateful = root.findViewById<StatefulLayout>(R.id.stateful)
        stateful.showContent()
        val recyclerView =  root.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = PlayBottomSheetRecyclerView(data, item.id!!)
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.adapter = adapter
        root.findViewById<Button>(R.id.playlistBottomSheetConfirmBtn).setOnClickListener {
            val toSave = mutableListOf<TrackModel>()
            val toDelete = mutableListOf<TrackModel>()
             adapter.checked.forEach { x->
                 toSave.add((item.clone() as TrackModel).apply { playlistID = x })
             }
            adapter.unchecked.forEach { x->
                toDelete.add((item.clone() as TrackModel).apply { playlistID = x })
            }
            lifecycleScope.launch(Dispatchers.Main) {
               database.trackDao().save(*toSave.toTypedArray())
            }
            lifecycleScope.launch(Dispatchers.Main) {
                database.trackDao().delete(*toDelete.toTypedArray())
            }
            this@PlaylistBottomSheet.dismiss()
        }
    }

    companion object {
        fun newInstance(item: TrackModel): PlaylistBottomSheet {
            return PlaylistBottomSheet(item)
        }
    }

    class PlayBottomSheetRecyclerView(val data: List<PlaylistAndTrack>, val trackId : Int) : RecyclerView.Adapter<PlayBottomSheetRecyclerViewHolder>() {

        var checked : MutableSet<Int> = mutableSetOf()
        var unchecked: MutableSet<Int> = mutableSetOf()
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PlayBottomSheetRecyclerViewHolder {
            return  PlayBottomSheetRecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.playlist_selector_item,parent,false))
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: PlayBottomSheetRecyclerViewHolder, position: Int) {
            val check = holder.card.findViewById<CheckBox>(R.id.playlist_selectorCheckBox)
            check.isChecked =  data[position].tracks.firstOrNull { x -> x.id.toInt() == trackId } != null
            check.setOnCheckedChangeListener { compoundButton, b ->
                 if(b) {
                     checked.add(data[position].playlist.databaseId)
                     unchecked.remove(data[position].playlist.databaseId)
                 }
                 else {
                     checked.remove(data[position].playlist.databaseId)
                     unchecked.add(data[position].playlist.databaseId)
                 }
             }
            holder.card.findViewById<TextView>(R.id.playlist_selectorText).text = data[position].playlist.title
            if(data[position].playlist.picture != null)
                Picasso.get().load(data[position].playlist.picture!!).into(holder.card.findViewById(R.id.playlist_selectorImage) as ImageView)
        }

    }

    class PlayBottomSheetRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val card : RelativeLayout  = itemView as RelativeLayout
    }
}
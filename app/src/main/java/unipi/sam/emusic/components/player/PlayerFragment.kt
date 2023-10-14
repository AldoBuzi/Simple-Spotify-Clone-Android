package unipi.sam.emusic.components.player

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import unipi.sam.emusic.main_module.MainActivity
import unipi.sam.emusic.R
import unipi.sam.emusic.base_components.BaseFragment
import unipi.sam.emusic.utils.ImageUtils
import unipi.sam.emusic.databinding.FragmentPlayerBinding
import unipi.sam.emusic.models.TrackModel
import unipi.sam.emusic.utils.toPx


@AndroidEntryPoint
class PlayerFragment : BaseFragment<FragmentPlayerBinding>(R.layout.fragment_player), PlayerCallbacks{

    //private var mMediaBrowser: MediaBrowserCompat? = null
    //var isSmall = false

    val viewModel : PlayerViewModel by viewModels ()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if(it.get("item") == null) return
            PlayerViewModel.item =  (it.get("item") as Array<TrackModel>).toMutableList()
            viewModel.setupUI()
            viewModel.setUpMediaController()
        }
        (activity as? MainActivity)!!.findViewById<FragmentContainerView>(R.id.player_container).y = 0f




    }

    override fun onStart() {
        super.onStart()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        handleWindowBars()
        addMotionLayoutListener()
        updateUI()
        handlePlayback()
        handleMiniPlayer()
        setupDatabaseRelated()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MusicBackgroundService","PlayerViewModel.item?.toTypedArray()) ${PlayerViewModel.item?.toTypedArray()}")



    }
    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()

    }

    override fun onBackPressed() {
    }
    companion object {
        @JvmStatic
        fun newInstance() =
            PlayerFragment().apply {
                arguments = Bundle().apply {
                    // put args
                }
            }
    }


    private fun updateUI(){
        viewModel.coverImage.observe(viewLifecycleOwner, Observer {
            ImageUtils
                .withCallback {
                    if(binding.playerMainContentImage.drawable != null)
                        viewModel.handleColorChange(binding.playerMainContentImage.drawable.toBitmap()) {
                            binding.container.setBackgroundColor(it)
                        }
                }
                .loadWithSpecificSize(it,binding.playerMainContentImage,Pair(300.toPx.toInt(),300.toPx.toInt()), R.drawable.music_placeholder)

        })
        viewModel.title.observe(viewLifecycleOwner, Observer {
            binding.playerTrackTitle.text = it
            binding.playerSmallTitle.text = it
        })
        binding.playerMinimizeBtn.setOnClickListener {
            binding.playerMotionLayout.transitionToEnd()
        }
        viewModel.artistName.observe(viewLifecycleOwner, Observer {
            binding.playerSmallSubTitle.text = it
            binding.playerArtistText.text = it
        })
    }

    private fun handleWindowBars(){
        PlayerViewModel.isSmall.observe(viewLifecycleOwner, Observer {
            if(!it) {
               (activity as? MainActivity)?.hideActionBar()
            }
        })
    }
    private fun addMotionLayoutListener(){
        val _container = (activity as? MainActivity)!!.findViewById<FragmentContainerView>(R.id.player_container)
        val startingPosition = (activity as? MainActivity)?.bottomNavigationView?.y
        binding.playerMotionLayout.addTransitionListener(object: MotionLayout.TransitionListener{
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
                viewModel.handleAnimationStart()
            }

            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
                 _container.y = - ( progress) * 76.toPx
                (activity as? MainActivity)?.bottomNavigationView?.y =  startingPosition!!.toFloat() + (1f - progress) * 80.toPx
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                viewModel.handleAnimationComplete(motionLayout!!.progress)

            }

            override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float){
            }

        })
    }
    private fun handlePlayback(){
        viewModel.position.observe(viewLifecycleOwner, Observer {
            val animator = ValueAnimator.ofInt(binding.playerTrackSlider.value.toInt(), it)
            animator.duration = 900
            animator.addUpdateListener { animation -> binding.playerTrackSlider.setValue((animation.animatedValue as Int).toFloat()) }
            animator.start()
        })
        binding.playerTrackSlider.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser -> if(fromUser) viewModel.seekTrackToTime(value.toInt()) })
        binding.playerPlayTrackBtn.setOnClickListener {
            viewModel.handlePlayButton()
        }
        binding.playerSmallPlayBtn.setOnClickListener {
            viewModel.handlePlayButton()
        }
        viewModel.isPlaying.observe(viewLifecycleOwner, Observer {
            binding.playerPlayTrackBtn.setImageDrawable(ResourcesCompat.getDrawable(resources,if(it) R.drawable.pause_main else R.drawable.play_main,null))
            binding.playerSmallPlayBtn.setImageDrawable(ResourcesCompat.getDrawable(resources,if(it) R.drawable.pause_small else R.drawable.play_small,null))
        })
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            binding.playerLoadingSpinner.visibility = if(it) View.VISIBLE else View.GONE
        })
        binding.playerPreviousTrackBtn.setOnClickListener {
            viewModel.handlePreviousTrackButton()
        }
        binding.playerNextTrackBtn.setOnClickListener {
            viewModel.handleNextTrackButton()
        }
    }

    private fun handleMiniPlayer(){
        PlayerViewModel.requestMiniPlayer.observe(viewLifecycleOwner, Observer {
            if(it) binding.playerMotionLayout.transitionToEnd()
        })

        PlayerViewModel.isReloadRequired.observe(viewLifecycleOwner, Observer {
            viewModel.isReloadRequired()
        })

        PlayerViewModel.grabPosition.observe(viewLifecycleOwner, Observer {
            val _container = (activity as? MainActivity)!!.findViewById<FragmentContainerView>(R.id.player_container)
            if(it) {
                Handler(Looper.getMainLooper()).post {
                    _container.y = (-76).toPx
                }

                PlayerViewModel.grabPosition.value = false
            }
        })
    }
    private fun setupDatabaseRelated(){
        viewModel.id.observe(viewLifecycleOwner, Observer {id ->
            lifecycleScope.launch (Dispatchers.Default) {
                if(viewModel.getLikeIconStatus())
                    binding.playerLikeTrackBtn.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.like_filled_icon,null))
                else
                    binding.playerLikeTrackBtn.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.like_icon,null))
            }
        })

        binding.playerLikeTrackBtn.setOnClickListener {
            lifecycleScope.launch  {
                var saveStatus = viewModel.saveTrack()
                if(saveStatus == true)
                    binding.playerLikeTrackBtn.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.like_filled_icon,null))
                else if(saveStatus == false)
                    binding.playerLikeTrackBtn.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.like_icon,null))

            }
        }
    }

}

interface PlayerCallbacks{
    fun onBackPressed()
}
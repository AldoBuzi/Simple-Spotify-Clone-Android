package unipi.sam.emusic.commons

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import unipi.sam.emusic.R

class SnackBarProvider {

    companion object{
        fun getSnackBar(activity: AppCompatActivity, title: String) : Snackbar{
            return Snackbar.make(
                activity.window.decorView.rootView,
                title,
                Snackbar.LENGTH_SHORT
            )
                .setAnchorView(activity.findViewById(R.id.bottom_navigation))
        }
        fun getSnackBarWithView(activity: Activity, view: View, title: String) : Snackbar{
            return Snackbar.make(
                activity.window.decorView.rootView,
                title,
                Snackbar.LENGTH_SHORT
            )
                .setAnchorView(view)
        }
    }
}
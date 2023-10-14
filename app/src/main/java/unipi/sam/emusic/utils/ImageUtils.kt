package unipi.sam.emusic.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import unipi.sam.emusic.R

class ImageUtils {


     companion object{
          val TAG: String = "ImageUtils";
          var callback: (()->Unit)? = null
          val listener = object : Callback{
               override fun onSuccess() {
                    callback?.invoke()
                    callback = null
               }
               override fun onError(e: java.lang.Exception?) {
                    callback = null
               }
          }
          fun loadImageFromUrl(imageUrl: String, imageView: ImageView, view: View) {
               loadImageFromUri(Uri.parse(imageUrl), imageView, view, null)
          }
          fun loadImageFromUrl(imageUrl: String, imageView: ImageView, callback: () -> Unit) {
               loadImageFromUri( Uri.parse(imageUrl), imageView, callback, null)
          }
          fun loadImageFromUrl(
               imageUrl: String,
               imageView: ImageView,
               view: View,
               placeholder: Int?
          ) {
               loadImageFromUri(Uri.parse(imageUrl), imageView, view, placeholder)
          }
          fun loadImageFromUri(
               imageUri: Uri,
               imageView: ImageView,
               view: View,
               placeholder: Int?
          ) {
               try {
                    val uri = imageUri
                    Log.d(TAG, "loadImageFromUri uri $uri")
                    Picasso.get()
                         .load(uri)
                         .placeholder(placeholder ?: R.drawable.music_placeholder)
                         .fit()
                         .centerCrop()
                         .into(imageView, listener) // solved with centerCrop
               } catch (e: Exception) {
                    Picasso.get().load(placeholder ?: R.drawable.music_placeholder).fit().centerCrop().into(imageView)
                    Log.e(TAG, "image load error $e")
               }
          }
          fun loadImageFromUri(
               imageUri: Uri,
               imageView: ImageView,
               callback: () -> Unit,
               placeholder: Int?
          ) {
               try {
                    Picasso.get()
                         .load(imageUri)
                         .placeholder(placeholder ?: R.drawable.music_placeholder)
                         .fit()
                         .centerCrop()
                         .into(imageView, object: Callback{
                              override fun onSuccess() {
                                   callback.invoke()
                              }
                              override fun onError(e: java.lang.Exception?) {}
                         }) // solved with centerCrop
               } catch (e: Exception) {
                    Picasso.get().load(placeholder ?: R.drawable.music_placeholder).fit().centerCrop().into(imageView)
                    Log.e(TAG, "image load error $e")
               }
          }
          fun loadWithSpecificSize(imageUrl: String, imageView: ImageView, size: Pair<Int,Int>, placeholder: Int? = null){
               try {
                    Picasso.get()
                         .load(imageUrl)
                         .resize(size.first,size.second)
                         .centerCrop()
                         .into(imageView, listener) // solved with centerCrop
               } catch (e: Exception) {
                    Picasso.get().load(placeholder ?: R.drawable.music_placeholder).fit().centerCrop().into(imageView)
                    Log.e(TAG, "image load error $e")
               }
          }
          fun loadForAnimation(imageView: ImageView,url:String, callback: ((draw:Drawable?)->Unit)? = null){
               Glide
                    .with(imageView)
                    .load(url)
                    .apply(
                         RequestOptions().dontTransform() // this line
                    )
                    .listener(object : RequestListener<Drawable> {

                         override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>?, isFirstResource: Boolean): Boolean {
                              return false
                         }

                         override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                              callback?.invoke(resource)
                              return false
                         }

                    })
                    .into(imageView)
          }

          fun withCallback(callback: ()->Unit) : ImageUtils.Companion{
               this.callback = callback
               return this
          }

     }
}
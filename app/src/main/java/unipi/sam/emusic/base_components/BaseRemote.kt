package unipi.sam.emusic.base_components

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import unipi.sam.emusic.api.ApiClient
import unipi.sam.emusic.api.RestApiInterface
import unipi.sam.emusic.commons.Application
import unipi.sam.emusic.commons.LayoutType
import unipi.sam.emusic.ux_classes.Widget
import javax.inject.Inject


open class BaseRemote  @Inject constructor(){
    //val apiService: RestApiInterface = ApiClient.client.create(RestApiInterface::class.java)
    @Inject lateinit var apiService : RestApiInterface
    //var apiService = client.create(RestApiInterface::class.java)
    val resources = Application.appContext!!.resources
    protected fun updateWidgets(vararg widget: Widget, widgets: MutableLiveData<MutableCollection<Widget>>){
        if(widgets.value == null){
            widgets.value = widget.toMutableList()
            return
        }
        widgets.value = widgets.value!!.apply { addAll(widget) }
    }
    protected fun baseCallback(layoutType: LayoutType,widgets: MutableLiveData<MutableCollection<Widget>>,heading : String? = null) : Callback<JsonObject> {
        return object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = response.body()
                if(response.isSuccessful && jsonObject?.isJsonObject == true)
                    updateWidgets(Widget(jsonObject, layoutType, heading?:""),widgets = widgets)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                updateWidgets(widgets = widgets)
            }
        }
    }
    protected fun baseCallbackMultiple(vararg layoutType: LayoutType,widgets: MutableLiveData<MutableCollection<Widget>>,heading : String? = null) : Callback<JsonObject> {
        return object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = response.body()
                if(response.isSuccessful && jsonObject?.isJsonObject == true)
                    for(key in layoutType)
                        updateWidgets(Widget(jsonObject, key,heading?:""),widgets = widgets)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                updateWidgets(widgets = widgets)
            }
        }
    }

}
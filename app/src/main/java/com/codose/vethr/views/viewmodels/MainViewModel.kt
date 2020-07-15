package com.codose.vethr.views.viewmodels

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.location.Location
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codose.vethr.network.NetworkRepository
import com.codose.vethr.network.response.searchResponse.PlaceResponse
import com.codose.vethr.network.response.weatherResponse.WeatherResponse
import com.codose.vethr.utils.Resource
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.SmartLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = NetworkRepository()
    private val context = getApplication<Application>().applicationContext
    val weatherData = MutableLiveData<Resource<WeatherResponse>>()
    val placeData = MutableLiveData<Resource<PlaceResponse>>()
    val location = MutableLiveData<Resource<Location>>()

    init {
        getLocation()
    }


    fun getWeatherData(lat:Double,long: Double){
        weatherData.value = Resource.Loading()
        viewModelScope.launch {
            weatherData.value = withContext(Dispatchers.IO){
                repository.getWeatherData(lat, long)
            }
        }
    }

    fun getLocation(){
        location.value = Resource.Loading()
        SmartLocation.with(context).location()
            .oneFix()
            .start {
                if(it!=null){
                    getWeatherData(it.latitude, it.longitude)
                    location.value = Resource.Success(it)
                }else{
                    location.value = Resource.Failure("Unable to get Location")
                }
            }
    }

    fun searchPlace(query: String){
        placeData.value = Resource.Loading()
        viewModelScope.launch {
            placeData.value = withContext(Dispatchers.IO){
                repository.searchPlaces(query)
            }
        }
    }
}
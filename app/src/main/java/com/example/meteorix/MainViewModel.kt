package com.example.meteorix

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val meteoriteApi: MeteoriteApi): ViewModel() {

    private val _meteoriteListLD = MutableLiveData<List<Meteorite>>()
    val meteoriteListLD: LiveData<List<Meteorite>> = _meteoriteListLD

    // loading and _loading

    init {
        fetchMeteorites()
    }

    private fun fetchMeteorites() {
        viewModelScope.launch {
            val response = meteoriteApi.getMeteorites()
            if (response.isSuccessful) {
                _meteoriteListLD.value = response.body()
            } else {
                // handle error
            }
        }
    }
}
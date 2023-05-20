package com.example.meteorix

import android.util.Log
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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    init {
        fetchMeteorites()
        Log.d("ZLOG","fetch fun ran")
    }

    private fun fetchMeteorites() {
        _isLoading.value = true
        viewModelScope.launch {
            val response = meteoriteApi.getMeteorites()
            if (response.isSuccessful) {
                _meteoriteListLD.value = response.body()
                _isLoading.value = false
            } else {
                _isLoading.value = false
            }
        }
    }
}
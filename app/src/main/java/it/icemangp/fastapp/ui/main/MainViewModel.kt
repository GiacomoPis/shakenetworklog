package it.icemangp.fastapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    fun startNetworkCall200() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                MainRepository.sampleNetworkCall("iceman")
            }
        }
    }

    fun startNetworkCall400() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                MainRepository.sampleNetworkCall("cakjfahfjhfoe")
            }
        }
    }
}
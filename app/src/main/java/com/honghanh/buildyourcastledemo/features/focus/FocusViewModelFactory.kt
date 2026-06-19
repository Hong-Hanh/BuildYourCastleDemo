package com.honghanh.buildyourcastledemo.features.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.honghanh.buildyourcastledemo.features.focus.data.FocusLocalRepository

class FocusViewModelFactory(
    private val repository: FocusLocalRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(FocusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FocusViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
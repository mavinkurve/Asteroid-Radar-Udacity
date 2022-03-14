package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application : Application) : AndroidViewModel(application) {

    private val repository by lazy { Repository(getDatabase(application)) }

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        Timber.i("Main view model init()")
            viewModelScope.launch {
                Timber.i("Refreshing repository")
                repository.refreshPicture()
                repository.refreshNeoWs()
            }
    }

    val picture = repository.picture

    private val _filter = MutableLiveData(Filter.ALL)
    fun setFilter(value: Filter) {
        _filter.postValue(value)
    }

    // Filter asteroid list by provided filter
    val asteroids = Transformations.switchMap(_filter) {
        when(it!!){
            Filter.TODAY -> repository.asteroids_today
            Filter.WEEK -> repository.asteroids_week
            else -> repository.asteroids
        }
    }

    /**
     * Factory for constructing view model with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}

enum class Filter {
    TODAY, WEEK, ALL
}

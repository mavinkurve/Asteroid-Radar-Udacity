package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.RadarDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.time.LocalDate

class Repository(private val database: RadarDatabase) {

    val picture: LiveData<PictureOfDay> = database.pictureDao.getPicture()

    val asteroids: LiveData<List<Asteroid>> =
        database.asteroidDao.getAllAsteroids()

    val asteroids_today: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroidsForToday()

    val asteroids_week: LiveData<List<Asteroid>> =
        database.asteroidDao.getAsteroidsForNextWeek()

       suspend fun refreshPicture() {
           Timber.i("Refreshing picture of day")
           withContext(Dispatchers.IO) {
               try {
                   val picture = Network.apiService.getPictureOfDay(BuildConfig.API_KEY).await()
                   Timber.d("picture of day is: ${picture.title}")
                   database.pictureDao.insert(picture.asDatabaseModel())
               }
               catch(e : Exception) {
                   Timber.e("Error occurred retrieving picture of day ${e.stackTrace}")
               }
           }
       }

    suspend fun refreshNeoWs() {
        Timber.i("Refreshing neo ws")
        withContext(Dispatchers.IO) {
            try {
                val response = Network.apiService.getNeoWs(BuildConfig.API_KEY,LocalDate.now().toString()).await()
                Timber.d(response)
                val _asteroidList = parseAsteroidsJsonResult(JSONObject(response))
                Timber.d("Asteroid received: $_asteroidList")
                database.asteroidDao.insertAll(*_asteroidList.asDatabaseModel())
            }
            catch(e : Exception) {
                Timber.e("Error occurred retrieving asteroid list ${e.stackTrace}")
                //throw e
            }
        }
    }

    fun deleteOlderAsteroids() {
        Timber.i("Deleting asteroids older than a day")
        database.asteroidDao.deletePastData()
    }
}



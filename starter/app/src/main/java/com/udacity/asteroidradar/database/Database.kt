package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.http.DELETE


@Dao
interface PictureDao {
    // Select first picture of type IMAGE (VIDEO media type will be ranked lower)
    @Query("select * from picture_of_day ORDER BY mediaType desc LIMIT 1")
    fun getPicture(): LiveData<PictureOfDay>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg picture: DatabasePictureOfDay)
}

@Dao
interface AsteroidDao {
    @Query("select * from asteroid ORDER by closeApproachDate ASC")
    fun getAllAsteroids() : LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid WHERE closeApproachDate BETWEEN date('now') AND date('now', '+7 day') ORDER BY closeApproachDate ASC")
    fun getAsteroidsForNextWeek(): LiveData<List<Asteroid>>

    @Query("SELECT * FROM asteroid WHERE closeApproachDate = date('now') ORDER BY closeApproachDate ASC")
    fun getAsteroidsForToday(): LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM asteroid WHERE DATE(closeApproachDate) < DATE('now')")
    fun deletePastData()
}

@Database(entities = [DatabasePictureOfDay::class, DatabaseAsteroid::class], version = 2, exportSchema = false)
abstract class RadarDatabase : RoomDatabase() {
    abstract val pictureDao: PictureDao
    abstract val asteroidDao : AsteroidDao
}

private lateinit var INSTANCE: RadarDatabase

fun getDatabase(context: Context): RadarDatabase {
    synchronized(RadarDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                RadarDatabase::class.java,
                "asteroid_radar"
            ).fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("drop TABLE picture_of_day")
    }
}

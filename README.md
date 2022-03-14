# Asteroid-Radar-Udacity
Udacity Android Kotlin Developer Project #2

To run locally, add a valid NASA API key to local gradle.properties file in the format
```
# NASA API Key
APIKEY="Add key here..."
```

## Rubric Requirements
1. The app displays a list of asteroids in the Main Screen by using a RecyclerView, when tapping an item the app opens Details screen.
2. The asteroids displayed in the screens are downloaded from the NASA API. On first run, app requests all asteroids occuring today onwards.
3. The NASA Astronomy image of the day is displayed in the Main Screen. If the image of the day is a video instead, last retrieved image will be shown.
4. The app can save the downloaded asteroids in the database and then display them also from the database. The app filters asteroids from the past by running a daily scheduled job to delete past asteroids in database.
5. The app downloads the next 7 days asteroids and saves them in the database once a day using workManager with requirements of internet connection and device plugged in. The app can display saved asteroids from the database even if internet connection is not available. Upon network connection unavailability, app silently falls back to saved list.
6. The app works correctly in talk back mode, it provides descriptions for all the texts and images

### Extra Credit
1. App supports Spanish as additional language and strings display correctly
2. App deletes older data from database once a day
3. All styles in the app look consistent using styles. 

## Note: Android Kotlin Gradle Update
Use the updated Gradle version in the `~/gradle/wrapper/gradle-wrapper.properties` file:
```
distributionUrl = https\://services.gradle.org/distributions/gradle-6.1.1-all.zip
```
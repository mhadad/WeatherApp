# WeatherApp
* Tech stack:
  - Jetpack compose
  - Jetpack navigation
  - Jetpack LiveData
  - Jetpack navigation
  - Kotlin coroutines
  - Room persistence
  - Fused Location Manager
  - Network connectivity manager
  - Retrofit
  - Gson
  - JUnit4
 
* Architecture style used:
  - MVVM in most of the project

* Defenesive coding mechanisms implemented:
  - Input validation using regex
  - Network availability checks
  - Null checks before accessing variables
  - Elvis operator
  - Plain old try and catch
  - Coroutines jobs cancellation

* POC functionality:
  - User can lookup weather conditions using:
    1- Zip Code for USA only ie. 92695
    2- City name lookup ie. Beverly Hills
    3- City name, State code, Country code ie. Beverly Hills,CA,US
    4- City name, Country code ie. Beverly Hills,US
    5- If location permissions were given the current weather conditions could be retrieved using location coordinates (Latitude & Longitude)
      

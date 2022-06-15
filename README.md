# Flower
Flower is an Android library that makes networking and database caching easy. It enables developers to fetch network resources and use them as is OR combine them with local database at single place with fault tolerant architecture.

## Why Flower?
- It helps you to handle different states (`Loading`, `Success`, `Error`) of resources efficiently.
- It helps you to use local data in case of network unavailability.
- It provides a fluid app experience by not blocking the `main thread` when accessing network/database resources.

You can find companion medium article [here](https://medium.com/@hadiyarajesh/android-networking-and-database-caching-in-2020-mvvm-retrofit-room-flow-35b4f897d46a)



## Installation

Add Gradle dependency as below
```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower:2.0.0")
}
```

## Usage
Sample model class
```kotlin
data class MyModel(
    val id: Long,
    val data: String
)
```

### Prerequisite
- Return type of `Room` DAO function must be `Flow<MyModel>` (Only if you're caching network resources using a local database)
```kotlin
@Dao
interface MyDao {
    @Query("SELECT * FROM Data")
    fun getLocalData(): Flow<MyModel>
}
```

- Return type of `Retrofit` api interface function must be `Flow<ApiResponse<MyModel>>`
```kotlin
interface MyApi {
    fun getRemoteData(): Flow<ApiResponse<MyModel>>
}
```

<br></br>
**1. Add `FlowCallAdapterFactory` as *CallAdapterFactory* in Retrofit builder**

```kotlin
Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(FlowCallAdapterFactory.create())
    .build()
```

<br></br>
**2. In Repository**
<br>

2.1. If you wants to fetch netwrok resources and cache into local database, use `networkBoundResource()` higher order function. It takes following functions as parameters

- *fetchFromLocal* - It fetch data from local database
- *shouldFetchFromRemote* - It decide whether network request should be made or use local data
- *fetchFromRemote* - It fetch data from network
- *processRemoteResponse* - It process response of network request. (e.g., save response headers)
- *saveRemoteData* - It saves result of network request (`MyModel`) to local database
- *onFetchFailed* - It perform provided action when network request fails (e.g., Non HTTP 200..300 response, exceptions etc)

```kotlin
fun getMyData(): Flow<Resource<MyModel>> {
    return networkBoundResources(
        fetchFromLocal = { myDao.getLocalData() },
        shouldFetchFromRemote = { localData -> localData == null },
        fetchFromRemote = { myApi.getRemoteData() },
        processRemoteResponse = { },
        saveRemoteData = { myDao.saveMyData(it) },
        onFetchFailed { errorMessage, statusCode -> }
    ).flowOn(Dispatchers.IO)
}
```

**OR**

2.2 If you only wants to fetch network resources, use `networkResource()` higher order function

```kotlin
fun getMyData(): Flow<Resource<MyModel>> {
    return networkResource(
        fetchFromRemote = { myApi.getRemoteData() },
        onFetchFailed { errorMessage, statusCode -> }
    ).flowOn(Dispatchers.IO)
}
```

<br></br>
**3. In ViewModel**
<br>
Collect/transform `Flow` to observe different state of resources (`Loading`, `Success`, `Error`)

```kotlin
sealed class UiState<out T> {
    object Empty : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T?) : UiState<T>()
    data class Error(val data: String?) : UiState<Nothing>()
}
```

```kotlin
private val _myData: MutableStateFlow<UiState<MyModel>> = MutableStateFlow(UiState.Empty)
val myData: StateFlow<UiState<MyModel>> = _myData

init {
    viewModelScope.launch {
        getMyData()
    }
}

suspend fun getMyData() = repository.getMyData().collect { response ->
    when (response.status) {
        Resource.Status.LOADING -> {
            _myData.value = UiState.Loading
        }
        Resource.Status.SUCCESS -> {
            _myData.value = UiState.Success(response.data)
        }
        Resource.Status.ERROR -> {
            _myData.value = UiState.Error(response.message)
        }
    }
}
```

<br></br>
**4. In Activity/Fragment**
<br>
Observe view model data in Activity/Fragment/Composable function to decide UI changes

```kotlin
lifecycleScope.launch {
    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.myData.collect { data ->
            when (data) {
                is UiState.Loading -> {
                    // Show loading
                }
                is UiState.Success -> {
                    // Show success
                }
                is UiState.Error -> {
                    // Show error
                }
                else -> { }

            }
        }
    }
}
```

## Sample
Sample app is provided [in this repository](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from remote api and save it to local persistent database in order to display it on UI.

## License
[MIT License](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

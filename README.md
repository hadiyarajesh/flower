<p align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="asset/flower-logo_white.png">
      <img width="300" height="300" src="asset/flower-logo_black.png">
    </picture>
</p>

Flower is a Kotlin library that makes networking and database caching easy. It enables developers to fetch network resources and use them as is OR combine them with local database at single place with fault-tolerant architecture.

![release](https://img.shields.io/github/v/release/hadiyarajesh/flower)
![contributors](https://img.shields.io/github/contributors/hadiyarajesh/flower)

## Why Flower?
- It helps you to handle different states (`Loading`, `Success`, `Error`, `Empty`) of resources efficiently.
- It helps you to use local data in case of network unavailability.
- It provides a fluid app experience by not blocking the `main thread` when accessing network/database resources.

You can find companion medium article [here](https://medium.com/@hadiyarajesh/android-networking-and-database-caching-in-2020-mvvm-retrofit-room-flow-35b4f897d46a)

## Installation

Flower is available as two specific modules, one for **Ktorfit** and one for **Retrofit**.

You can use the core module if you want to handle networking yourself.

### Ktorfit

This is a multiplatform module. It can be used in other Kotlin multiplatform projects, Android (Apps/Libs), on the JVM in general, with Kotlin-JS and so on...

It uses and provides [Ktorfit](https://github.com/Foso/Ktorfit) and you need to apply KSP to your project.

Apply the KSP Plugin to your project:
```gradle
plugins {
  id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}
```

Multiplatform example:
```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-ktorfit:2.0.3")

    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspJvm", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspAndroid", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspIosX64", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspJs", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
    add("kspIosSimulatorArm64", "de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
}
```

Android example:
```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-ktorfit:2.0.3")
    
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:1.0.0-beta09")
}
```

### Retrofit

This is an Android-Only module, so it can only be used in Android Apps/Libs.

This does not require the KSP plugin.

```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-retrofit:2.0.3")
}
```

### Core

This module is only the core module to handle the networking yourself.

**Only use this if you don't want to rely on Ktorfit or Retrofit.**

```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-core:2.0.3")
}
```

## Usage
Let's say you have a model class `MyModel` which you are fetching from network
```kotlin
data class MyModel(
    val id: Long,
    val data: String
)
```

### Prerequisite

- Return type of your caching system function must be `Flow<MyModel>`.

Android Room example:
```kotlin
@Dao
interface MyDao {
    @Query("SELECT * FROM Data")
    fun getLocalData(): Flow<MyModel>
}
```

- Return type of networking api function must be `Flow<ApiResponse<MyModel>>`

Ktorfit/Retrofit example:
```kotlin
interface MyApi {
    @GET("example")
    fun getRemoteData(): Flow<ApiResponse<MyModel>>
}
```

### Ktorfit

**1. Add `FlowerResponseConverter` as *ResponseConverter* in Ktorfit builder**

```kotlin
Ktorfit(
    BASE_URL,
    httpClient
).addResponseConverter(
    FlowerResponseConverter()
)
```


### Retrofit

**1. Add `FlowerCallAdapterFactory` as *CallAdapterFactory* in Retrofit builder**

```kotlin
Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(FlowerCallAdapterFactory.create())
    .build()
```

<br><br>
**2. In Repository**
<br>

2.1. If you want to fetch netwrok resources and cache into local database, use `dbBoundResource()` higher order function. It takes following functions as parameters

- *fetchFromLocal* - It fetch data from local database
- *shouldFetchFromRemote* - It decide whether network request should be made or use local data
- *fetchFromRemote* - It fetch data from network
- *processRemoteResponse* - It process response of network request. (e.g., save response headers)
- *saveRemoteData* - It saves result of network request (`MyModel`) to local database
- *onFetchFailed* - It perform provided action when network request fails (e.g., Non HTTP 200..300 response, exceptions etc)

```kotlin
fun getMyData(): Flow<Resource<MyModel>> {
    return dbBoundResources(
        fetchFromLocal = { myDao.getLocalData() },
        shouldMakeNetworkRequest = { localData -> localData == null },
        makeNetworkRequest = { myApi.getRemoteData() },
        processRequestResponse = { },
        saveRequestData = { myDao.saveMyData(it) },
        onRequestFailed { errorMessage, statusCode -> }
    ).flowOn(Dispatchers.IO)
}
```

**OR**

2.2 If you only want to fetch network resources, use `networkResource()` higher order function

```kotlin
fun getMyData(): Flow<Resource<MyModel>> {
    return networkResource(
        makeNetworkRequest = { myApi.getRemoteData() },
        onRequestFailed { errorMessage, statusCode -> }
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
    data class Loading(val data: T?) : UiState<out T>()
    data class Success<out T>(val data: T & Any) : UiState<T & Any>()
    data class Error(val msg: String?) : UiState<Nothing>()
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
        is Resource.Status.LOADING -> {
            val status = response.status as Resource.Status.LOADING
            _myData.value = UiState.Loading(status.data)
        }
        is Resource.Status.SUCCESS -> {
            val status = response.status as Resource.Status.SUCCESS
            _myData.value = UiState.Success(status.data)
        }
        is Resource.Status.EMPTY -> {
            _myData.value = UiState.Empty
        }
        is Resource.Status.ERROR -> {
            val status = response.status as Resource.Status.LOADING
            _myData.value = UiState.Error(status.message)
        }
    }
}
```

<br></br>
**4. In Activity/Fragment/Composable**
<br>
Observe view model data in Activity/Fragment/Composable function to drive UI changes

```kotlin
lifecycleScope.launchWhenCreated {
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
```

## Sample
Sample app is provided [in this repository](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from remote api and save it to local persistent database in order to display it on UI.

## License
[Apache License 2.0](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

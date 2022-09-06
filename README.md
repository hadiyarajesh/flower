<p align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="asset/flower-logo_white.png">
      <img width="300" height="300" src="asset/flower-logo_black.png">
    </picture>
</p>

Flower is a Kotlin multi-platform (originally, Android) library that makes networking and database caching easy. It enables developers to
fetch network resources and use them as is OR combine them with local database at single place with
fault-tolerant architecture.

![release](https://img.shields.io/github/v/release/hadiyarajesh/flower)
![contributors](https://img.shields.io/github/contributors/hadiyarajesh/flower)

## Why Flower?

- It helps you to handle different states (`Loading`, `Success`, `EmptySuccess`, `Error`) of
  resources efficiently.
- It helps you to use local data in case of network unavailability.
- It provides a fluid app experience by not blocking the `main thread` when accessing
  network/database resources.

You can find companion medium
article [here](https://medium.com/@hadiyarajesh/android-networking-and-database-caching-in-2020-mvvm-retrofit-room-flow-35b4f897d46a)

## Installation

Flower is primarily available in two modules, one for **Ktorfit** and the other for **Retrofit**.

If you want to handle networking yourself, you can also use the **core** module.

`$flowerVersion=3.0.0`
<br>
`$ktorFitVersion=1.0.0-beta12`

### Ktorfit
This module is multi-platform. It is suitable for use in Kotlin multi-platform projects, Android (Apps/Libraries), the JVM in general, Kotlin-JS, and so on...

It uses and provides [Ktorfit](https://github.com/Foso/Ktorfit) and you must use KSP in your project.

Apply the KSP Plugin to your project:
```gradle
plugins {
  id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}
```

**Multiplatform example**
```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-ktorfit:$flowerVersion")

    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
    add("kspJvm", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
    add("kspAndroid", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
    add("kspIosX64", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
    add("kspJs", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
    add("kspIosSimulatorArm64", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
}
```

**Android example**
```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-ktorfit:$flowerVersion")
    
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorFitVersion")
}
```

### Retrofit
This is an Android-only module, so it can only be used in Android Apps/Libs. It does not require the KSP plugin.

```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-retrofit:$flowerVersion")
}
```

### Core
This module only contains the _core code_ and allows you to handle the networking yourself.

**We Highly recommend you to use either Ktorfit or Retrofit module. Only use this if you don't want
to rely on Ktorfit or Retrofit.**

```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-core:$flowerVersion")
}
```

## Usage

Assume you have a model class called `MyModel` that you are retrieving from the network.

```kotlin
data class MyModel(
    val id: Long,
    val data: String
)
```

### Prerequisite
- If you want to save network response in a local database, your database caching system function must return a value of type `Flow<MyModel>`.

**Android Room example:**

```kotlin
@Dao
interface MyDao {
    @Query("SELECT * FROM Data")
    fun getLocalData(): Flow<MyModel>
}
```

- Return type of networking api function must be `ApiResponse<MyModel>` (
  or `Flow<ApiResponse<MyModel>>` if you're retrieving a flow of data from server)

**Ktorfit/Retrofit example:**
```kotlin
interface MyApi {
    @GET("data")
    suspend fun getRemoteData(): ApiResponse<MyModel>
    // OR
    @GET("data")
    fun getRemoteData(): Flow<ApiResponse<MyModel>>
}
```

<br><br>
#### 1. Add CallAdapterFactory/ResponseConverter in networking client

**Ktorfit**
Add `FlowerResponseConverter` as *ResponseConverter* in Ktorfit builder.

```kotlin
Ktorfit.Builder()
  .baseUrl(BASE_URL)
  .httpClient(ktorClient)
  .responseConverter(FlowerResponseConverter())
  .build()
```

**Retrofit**
Add `FlowerCallAdapterFactory` as *CallAdapterFactory* in Retrofit builder

```kotlin
Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(FlowerCallAdapterFactory.create())
    .build()
```

<br><br>
####2. Make network request (and save data) in Repository

2.1. If you want to fetch network resources and save into local database, 
use `dbBoundResource()` higher order function 
(or `dbBoundResourceFlow()` function if you're retrieving a flow of data from server). 
It takes following functions as parameters.

- *fetchFromLocal* -  A function to retrieve data from local database
- *shouldMakeNetworkRequest* - Decides whether or not to make network request
- *makeNetworkRequest* - A function to make network request
- *processNetworkResponse* - A function to process network response (e.g., saving response headers before saving actual data)
- *saveResponseData* - A function to save network response (`MyModel`) to local database
- *onNetworkRequestFailed* - An action to perform when a network request fails

```kotlin
fun getMyData(): Flow<Resource<MyModel>> {
    return dbBoundResources(
        fetchFromLocal = { myDao.getLocalData() },
        shouldMakeNetworkRequest = { localData -> localData == null },
        makeNetworkRequest = { myApi.getRemoteData() },
        processNetworkResponse = { },
        saveResponseData = { myDao.saveMyData(it) },
        onNetworkRequestFailed { errorMessage, statusCode -> }
    ).flowOn(Dispatchers.IO)
}
```

**OR**

2.2 If you only want to fetch network resources without saving it in local database, 
use `networkResource()` higher order function.
(or `networkResourceFlow()` function if you're retrieving a flow of data from server)

```kotlin
fun getMyData(): Flow<Resource<MyModel>> {
    return networkResource(
        makeNetworkRequest = { myApi.getRemoteData() },
        onNetworkRequestFailed { errorMessage, statusCode -> }
    ).flowOn(Dispatchers.IO)
}
```

<br></br>
**3. Collect `Flow` to observe different state of resources (`Loading`, `Success`, `Error`) In ViewModel**

```kotlin
// A model class to re-present UI state
sealed class UiState<out T> {
    object Empty : UiState<Nothing>()
    data class Loading(val data: T?) : UiState<out T>()
    data class Success<out T>(val data: T & Any) : UiState<T & Any>()
    data class Error(val msg: String?) : UiState<Nothing>()
}
```

```kotlin
private val _myData: MutableStateFlow<UiState<MyModel>> = MutableStateFlow(UiState.Empty)
val myData: StateFlow<UiState<MyModel>> = _myData.asStateFlow()

init {
    viewModelScope.launch {
        getMyData()
    }
}

suspend fun getMyData() = repository.getMyData().collect { response ->
    when (response.status) {
        is Resource.Status.Loading -> {
            val status = response.status as Resource.Status.Loading
            _myData.value = UiState.Loading(status.data)
        }
      
        is Resource.Status.Success -> {
            val status = response.status as Resource.Status.Success
            _myData.value = UiState.Success(status.data)
        }
      
        // EmptySuccess is for body-less successful HTTP responses like 201
        is Resource.Status.EmptySuccess -> {
            _myData.value = UiState.Empty
        }
      
        is Resource.Status.Error -> {
            val status = response.status as Resource.Status.Error
            _myData.value = UiState.Error(status.message)
        }
    }
}
```

<br></br>
**4. Observe data in Activity/Fragment/Composable function to drive UI changes**

```kotlin
lifecycleScope.launchWhenStarted {
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
            else -> {}
        }
    }
}
```

## Sample

Two sample apps are provided in this repository
1. [XML View based app](https://github.com/hadiyarajesh/flower/tree/master/app) - It fetch random quote from remote api and save it to local database in order to display it on UI.
2. [Jetpack Compose based app](https://github.com/hadiyarajesh/flower/tree/master/compose-app) - It fetches unsplash images from [Picsum](https://picsum.photos) and display it on UI.


## License

[Apache License 2.0](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

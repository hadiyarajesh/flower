<p align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="asset/flower-logo_white.png">
      <img width="300" height="300" src="asset/flower-logo_black.png">
    </picture>
</p>

Flower is a Kotlin library that makes networking and database caching easy. It enables developers to
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

Flower is mainly available as two specific modules, one for **Ktorfit** and one for **Retrofit**.

You may also use the **core** module if you want to handle networking yourself.

`$flowerVersion=3.0.0`

### Ktorfit

This is a multiplatform module. It can be used in other Kotlin multiplatform projects, Android (
Apps/Libs), on the JVM in general, with Kotlin-JS and so on...

It uses and provides [Ktorfit](https://github.com/Foso/Ktorfit) and you need to apply KSP to your
project.
`$ktorFitVersion=1.0.0-beta12`

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

This is an Android-Only module, so it can only be used in Android Apps/Libs. It does not require the
KSP plugin.

```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-retrofit:$flowerVersion")
}
```

### Core

This module only contains the core code and allows you to handle the networking yourself.

**We Highly recommend you to use either Ktorfit or Retrofit module. Only use this if you don't want
to rely on Ktorfit or Retrofit.**

```gradle
dependencies {
    implementation("io.github.hadiyarajesh:flower-core:$flowerVersion")
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

- Return type of networking api function must be `ApiResponse<MyModel>` (
  or `Flow<ApiResponse<MyModel>>` if you want to get as Flow from server)

Ktorfit/Retrofit example:

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
#### 1. Add CallAdapterFactory/ResponseConverter

**Ktorfit**

Add `FlowerResponseConverter` as *ResponseConverter* in Ktorfit builder

```kotlin
Ktorfit(
    BASE_URL,
    httpClient
).addResponseConverter(
    FlowerResponseConverter()
)
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
####2. In Repository

2.1. If you want to fetch network resources and cache into local database, use `dbBoundResource()`
higher order function. It takes following functions as parameters

- *fetchFromLocal* -  A function to retrieve data from local database
- *shouldFetchFromRemote* - Decide whether or not to make network request
- *fetchFromRemote* - A function to make network request
- *processRemoteResponse* - A function to process network response (e.g., saving response headers before saving actual data)
- *saveRemoteData* - A function to save network response (`MyModel`) to local database
- *onFetchFailed* - An action to perform when a network request fails

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
            else -> {}

        }
    }
}
```

## Sample

Sample app is
provided [in this repository](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample)
. It fetch random quote from remote api and save it to local persistent database in order to display
it on UI.

## License

[Apache License 2.0](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

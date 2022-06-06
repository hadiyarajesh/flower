# Flower
Flower is an Android library that makes networking and database caching easy. It enables developers to fetch network resources and use them as is OR combine them with local database at single place with fault tolerant architecture.

## Why Flower?
- It helps you to handle different states (`Loading`, `Success`, `Error`) of resources efficiently.
- It helps you to use local data in case of network unavailability.
- It provides a fluid app experience by not blocking the `main thread` when accessing network/database resources.

You can find companion medium article [here](https://medium.com/@hadiyarajesh/android-networking-and-database-caching-in-2020-mvvm-retrofit-room-flow-35b4f897d46a)



## Installation

Add Gradle dependency as below
```
dependencies {
    implementation("io.github.hadiyarajesh:flower:2.0.0")
}
```

## Usage

**Prerequisite**
- Return type of `Room` DAO method must be `Flow<YourModelClass>` (Only if you're caching network resources using a local database)
- Return type of `Retrofit` api interface method must be `Flow<ApiResponse<YourModelClass>>`

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
- *saveRemoteData* - It saves result of network request (`YourModelClass`) to local database
- *onFetchFailed* - It perform provided action when network request fails (e.g., Non HTTP 200..300 response, exceptions etc)

```kotlin
fun getSomething(): Flow<Resource<YourModelclass>> {
    return networkBoundResources(
        fetchFromLocal = { yourDaoclass.getFromDatabase() },
        shouldFetchFromRemote = { it == null },
        fetchFromRemote = { apiInterface.getFromRemote() },
        processRemoteResponse = { },
        saveRemoteData = { yourDaoclass.saveYourData(it) },
        onFetchFailed {_, _ -> }
    ).flowOn(Dispatchers.IO)
}
```

**OR**

2.2 If you only wants to fetch network resources, use `networkResource()` function

```kotlin
fun getSomething(): Flow<Resource<YourModelclass>> {
    return networkResource(
        fetchFromRemote = { apiInterface.getFromRemote() },
        onFetchFailed {_, _ -> }
    ).flowOn(Dispatchers.IO)
}
```

<br></br>
**3. In ViewModel**
Collect/transform `flow` to observe different state of resources (`Loading`, `Success`, `Error`)

```kotlin
val someVariable: LiveData<Resource<YourModelClass>> = repository.getSomething().map {
    when (it.status) {
        Resource.Status.LOADING -> {
            Resource.loading(null)
        }
        Resource.Status.SUCCESS -> {
            Resource.success(it.data)
        }
        Resource.Status.ERROR -> {
            Resource.error(it.message!!, null)
        }
    }
}.asLiveData(viewModelScope.coroutineContext)
```

<br></br>
**4. In Activity/Fragment**
Observe view model data in Activity/Fragment to decide UI changes

```kotlin
viewModel.someVariable.observer(this, Observer {
    when (it.status) {
        Resource.Status.LOADING -> {
            //Show loading message
        }
        Resource.Status.SUCCESS -> {
            //Show success message
        }
        Resource.Status.ERROR -> {
            //Show error message
        }
    }
})
```

## Sample
Sample app is provided [in this repository](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from remote api and save it to local persistent database in order to display it on UI.

## License
[MIT License](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

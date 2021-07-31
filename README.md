# Flower
Super cool Android library to manage networking and database caching with ease. It allows developers to use remote resources on-the-fly OR Combine remote resources and local persistence data caching in single place with fault tolerant architecture. It's built on top of [Retrofit](https://github.com/square/retrofit) and [Kotlin Flow](https://kotlinlang.org/docs/flow.html).

## Why do we even need this library?
- It helps you to handle different states of resources (LOADING, SUCCESS, FAILURE) efficiently.
- It does not block main thread while accessing network/database resources, thus providing fluid experience for your users.
- It leverage Kotlin idiomatic api, thus doing all hard stuffs behind a simple function call.

You can find companion medium article [here](https://medium.com/@hadiyarajesh/android-networking-and-database-caching-in-2020-mvvm-retrofit-room-flow-35b4f897d46a)



## Installation

Add Gradle dependency as below
```
dependencies {
    //Groovy DSL
    implementation "io.github.hadiyarajesh:flower:2.0.0"
    Kotlin DSL
    implementation("io.github.hadiyarajesh:flower:2.0.0")
}
```

## Usage

**Prerequisite**
- Your Room DAO method must return ```Flow<YourModelClass>``` (If you're using database caching)
- Your Retrofit API method must return ```Flow<ApiResponse<YourModelClass>>```

<br></br>
**1. Add ```FlowCallAdapterFactory``` as *CallAdapterFactory* in Retrofit builder**

```kotlin
Retrofit.Builder()
    .baseUrl(BASE_API_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(FlowCallAdapterFactory.create())
    .build()
```

<br></br>
**2. Inside Repository**

If you want to use remote resources backed by local database caching, use *networkBoundResource()* function. This function takes following functions as lambda parameter 
- *fetchFromLocal* - It retrieve data from local database
- *shouldFetchFromRemote* - It decide whether network request should be made or use local persistent data if available
- *fetchFromRemote* - It retrieve data from network request
- *processRemoteResponse* - It process process result of network request. (e.g., save response headers)
- *saveRemoteData* - It saves result of network request to local persistent database
- *onFetchFailed* - It perform provided actions when network request fails (Non HTTP 200..300 response, exceptions etc)

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

If you want to use remote resources on-the-fly (without caching into local database), use *networkResource()* function

```kotlin
fun getSomething(): Flow<Resource<YourModelclass>> {
    return networkBoundResources(
        fetchFromRemote = { apiInterface.getFromRemote() },
        onFetchFailed {_, _ -> }
    ).flowOn(Dispatchers.IO)
}

```

<br></br>
**2. Inside ViewModel**

Collect/transform flow to get different state of resources: LOADING, SUCCESS or ERROR

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
**3. Inside Activity/Fragment**

Observe it in Activity/Fragment to make UI changes

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

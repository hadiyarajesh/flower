# Flower
Super cool library for Android to manage database caching and networking with ease. It helps you to handle all scenario of API request(success/error/loading) in one place along with database caching. It's inspired from [Google's Github Browser Sample](https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample/). It's built on top of [Retrofit](https://github.com/square/retrofit) and use powerful and elegant kotlin flow api.

You can find companion medium article [here](https://medium.com/@hadiyarajesh/android-networking-and-database-caching-in-2020-mvvm-retrofit-room-flow-35b4f897d46a)

## Installation
Add JitPack to your project level build.gradle file
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add Gradle dependency as
```
dependencies {
    implementation 'com.github.hadiyarajesh:flower:1.1.0'
}
```

## Usage

**Prerequisite**
- Your Room Dao method must return ```Flow<YourModelClass>```
- Your Retrofit API method must return ```Flow<ApiResponse<YourModelClass>>```
- Add ```FlowCallAdapterFactory``` as *CallAdapterFactory* in your retrofit builder


**1. In Repository class**

Return the *networkBoundResource()* function from repository. This function takes following functions as parameter 

- *fetchFromLocal* - It fecth data from local database
- *shouldFetchFromRemote* - It decide whether network request should be made or use local persistent data if available
- *fetchFromRemote* - It perform network request operation
- *processRemoteResponse* - It process result of network response (if requires)
- *saveRemoteData* - It saves result of network request to local persistent database
- *onFetchFailed* - It handle network request failure scenario (Non HTTP 200..300 response, exceptions etc)

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

OR

Return the *networkResource()* function from repository if you want to use network resources on-the-fly without caching them into local database

```kotlin
fun getSomething(): Flow<Resource<YourModelclass>> {
    return networkBoundResources(
        fetchFromRemote = { apiInterface.getFromRemote() },
        onFetchFailed {_, _ -> }
    ).flowOn(Dispatchers.IO)
}

```

**2. In View Model class**

Collect/transform flow to get 3 different state of request: LOADING, SUCCESS or ERROR

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

**3. In Activity/Fragment class**

Observe it in your Activity/Fragment as 

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
Sample app is provided [here](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from remote api and save it to local persistent database in order to display it on UI.

## License
[MIT License](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

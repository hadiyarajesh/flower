# Flower
Super cool library for Android to manage database caching and networking with ease. It helps you to handle all scenario of API request(success/error/loading) in one place along with database caching. It's inspired from [Goolge's Github Browser Sample](https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample/). It's built on top of [Retrofit](https://github.com/square/retrofit) and use powerful and elegant kotlin flow api.

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
    implementation 'com.github.hadiyarajesh:flower:1.0.0'
}
```

## Usage

**Prerequisite**
- Your Dao method must return ```Flow<YourModelClass>```
- Your api method must return ```Flow<ApiResponse<YourModelClass>>```
- Add **FlowCallAdapterFactory** as addCallAdapterFactory in your retrofit instance


1. In repository class, return the *networkBoundResource()* function. This function takes following function as parameter 

- *fetchFromLocal* - It fecth data from local database
- *shouldFetchFromRemote* - It decide whether need to do network request or use local persistent data
- *fetchFromRemote* - It perform network request operation
- *processRemoteResponse* - It process result of network response (if needed)
- *saveRemoteData* - It saves result of network request to local persistent database
- *onFetchFailed* - It executes if network request fails

Sample call to *networkBoundResource()* should look like this

```
fun getSomething(): Flow<Resource<YourModelClass>> {
    return networkBoundResource(
      fetchFromLocal = { yourDaoClass.getFromDatabase() },
      shouldFetchFromRemote = { it == null },
      fetchFromRemote = { apiInterface.getFromRemote() },
      processRemoteResponse = { },
      saveRemoteData = { yourDaoClass.saveYourData(it) },
      onFetchFailed = { _, _ -> }
      ).flowOn(Dispatchers.IO)
}
```

2. In view model class, collect or transform flow to get 3 different state of on-going request, LOADING, SUCESS or ERROR
```
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

Now you can observe it in your Activity/Fragment.

## Sample
Sample app is provided in this repository. You can find it [here](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from remote api and save it to local persistent database in order to display it on UI.

## License
[MIT License](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

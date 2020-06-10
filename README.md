# Flower
Super cool library to manage database caching and networking with ease for Android. It helps you to handle API success/error response in one place along with database caching. It is inspired from [Goolge's Github Browser Sample](https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample/). It is built on top of [Retrofit](https://github.com/square/retrofit) and using powerful and elegant kotlin flow api.

## Built with
[Kotlin](https://kotlinlang.org/) - A modern programming language for Android/JVM that makes developers happier.

[Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous programming

[Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/) - A cold asynchronous data stream that sequentially emits values and completes normally or with an exception.

## Installation
Add JitPack to your build.gradle file
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

1. Your Dao method must return ```Flow<YourModelClass>```
2. Your api method must return ```Flow<ApiResponse<YourModelClass>>```
3. Add **FlowCallAdapterFactory** as addCallAdapterFactory in your retrofit instance
4. In repository class

  Return the *networkBoundResource()* function. This function takes following function as parameter 

- *fetchFromLocal* - It fecth data from local database
- *shouldFetchFromRemote* - It decide whether need to do network request or use local persistent data
- *fetchFromRemote* - It perform network request operation
- *processRemoteResponse* - It process result of network response (if needed)
- *saveRemoteData* - It saves result of network request to local persistent database
- *onFetchFailed* - It executes if network request fails

Sample call to *networkBoundResource()* should look like this

```
return networkBoundResource(
  fetchFromLocal = { yourDaoClass.getFromDatabase() },
  shouldFetchFromRemote = { it == null },
  fetchFromRemote = { apiInterface.getFromRemote() },
  processRemoteResponse = { },
  saveRemoteData = { yourDaoClass.saveYourData(it) },
  onFetchFailed = { _, _ -> }
  ).flowOn(Dispatchers.IO)
```

5. In view model class
Collect or transform flow in to get different state of on-going request, LOADING, SUCESS or ERROR
```
val anything: LiveData<Resource<YourModelClass>> = repository.getSomething().map {
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

Now you can observe it in your Activity/Fragment

## Samples
Sample app is provided in this repository. You can find it [here](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from api and save it to local persistent database to display in UI.

## License
[MIT License](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

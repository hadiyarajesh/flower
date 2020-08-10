# Flower
Super cool library for Android to manage database caching and networking with ease. It helps you to handle all scenario of API request(success/error/loading) in one place along with database caching. It's inspired from [Goolge's Github Browser Sample](https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample/). It's built on top of [Retrofit](https://github.com/square/retrofit) and use powerful and elegant kotlin flow api.

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
    implementation 'com.github.hadiyarajesh:flower:1.0.0'
}
```

## Usage

**Prerequisite**
- Your Room Dao method must return ```Flow<YourModelClass>```
- Your Retrofit API method must return ```Flow<ApiResponse<YourModelClass>>```
- Add ```FlowCallAdapterFactory``` as *CallAdapterFactory* in your retrofit builder


**1. In repository class**

return the *networkBoundResource()* function. This function takes following functions as parameter 

- *fetchFromLocal* - It fecth data from local database
- *shouldFetchFromRemote* - It decide whether network request should be made or use local persistent data if available
- *fetchFromRemote* - It perform network request operation
- *processRemoteResponse* - It process result of network response (if requires)
- *saveRemoteData* - It saves result of network request to local persistent database
- *onFetchFailed* - It handle network request failure scenario (Non HTTP 200..300 response, exceptions etc)

Sample call to *networkBoundResource()* should look like this

![repository](https://user-images.githubusercontent.com/12107428/86233737-83067500-bbb3-11ea-96b6-76eb199fbef4.png)


**2. In view model class**

Collect or transform flow to get 3 different state of on-going request, LOADING, SUCESS or ERROR

![viewmodel](https://user-images.githubusercontent.com/12107428/86233783-99143580-bbb3-11ea-96c5-77fa00bdf21e.png)


**3. In Activity/Fragment class**

Now you can observe it in your Activity/Fragment as 

![activity](https://user-images.githubusercontent.com/12107428/89805013-9ca3c080-db52-11ea-9f03-6147c13e0574.png)


## Sample
Sample app is provided in this repository. You can find it [here](https://github.com/hadiyarajesh/flower/tree/master/app/src/main/java/com/hadiyarajesh/flowersample).
It fetch random quote from remote api and save it to local persistent database in order to display it on UI.

## License
[MIT License](https://github.com/hadiyarajesh/flower/blob/master/LICENSE)

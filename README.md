 Intoo Tracker
 
## Download
 
 -  Add the following to your project level `build.gradle`:
 
 ```gradle
 allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```

 -  Add this to your app `build.gradle`:

```gradle
dependencies {
       implementation 'com.github.intoo-ir:intoo-audience-tracker-lib:v0.2.0-alpha'
}
```

## Usage

first initialise tracker

```
 val tracker = Tracker(this@MainActivity)
 ```
then start tracker

```
 tracker.start(startService = true)
 ```
 when start tracker with service you can stop  service with this code
 ```
 tracker.stopService()
 ```
 
 for save profile use this sample
 
 ```
Tracker.saveProfile(this, userAge = 25, userGender = Tracker.MALE)
 ```
 
## Contributing

Pull requests with bug fixes or new features are always welcome :), but please, send me a separate pull request for each bug fix or feature. Also, you can [contact](mailto:sajad.zohrei@yahoo.com) me to discuss a new feature before implementing it.

## Developed By
Sajad Zohrei: <sajad.zohrei@yahoo.com>


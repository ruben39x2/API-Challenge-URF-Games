# URF Games (Android app)
Android application based on the API Challenge of Riot Games. This app uses the League of Legends API, by implementing the new endpoint and allowing the user to check the recent URF games of his/her region, see the stats of the matches and the stats of the summoners.


# Asynchronous tasks
The requests are always managed through asynchronos task, so that the UI never gets stuck.

# Permissions of the app
- Accessing the internet: In order to send the petitions to the Riot Games servers.
- Writing / reading data from the SDCard: In order to create a log file and read the version of ddragon to use.

# Running / testing the app
This began as a gradle project created on Android Studio. All the sources are in the git, so you can download them through a pull request and test / run them. Be aware! The key for the API requests must be set in the APIKey class.
This can be a bit complex.
- In order to run the app faster, you can simply download the .apk of the apk folder and test it on a device or a emulator.

# More info
- [League of Legends API](developer.riotgames.com/api/methods)
- [API Challenge](https://developer.riotgames.com/discussion/riot-games-api/show/bX8Z86bm)

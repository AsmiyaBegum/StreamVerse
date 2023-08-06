# StreamVerse
Stream Verse

**Purpose** : 

The objective of this application is to allow users to easily discover and watch a wide range of movies available on the platform. Users can access and view the movies within the application. 

**Application Screenshots** :

![Home-Screen](https://github.com/AsmiyaBegum/StreamVerse/blob/master/applicationGIF/home_screen.gif)

**Goals** : 

● The home screen should prominently showcase the latest trends in movies.

● Movies should be categorised and listed under various categories for easy browsing.

● Freely available movies should be highlighted to indicate that they can be watched without any cost.

● The top 10 movies should be displayed to provide users with popular options.

● Users should have the ability to watch movie trailers. 

● When a movie is played, it should automatically adjust to landscape orientation, and users should be able to switch the screen orientation as desired. 

● Users should be able to control playback features such as play, pause, seek forward, seek backward. 

● Users should have the option to adjust volume and screen brightness while watching a movie. 

● A screen lock feature should be available to prevent accidental touches while watching a movie. 

● Users should be able to share the watched movie video with others via email and chat applications. 

**Proposed Flow** : 

The application will have three screens to facilitate different functionalities: 

● **Home Screen**: This screen will display a list of available movies. Users can browse through the movies and select the one they want to explore further. 

● **Preview Screen**: When a movie is selected from the home screen, the Preview Screen will provide detailed information about the movie, including its synopsis, cast, genre, and ratings. Users will have the option to watch the trailer to get a glimpse of the movie. They can also choose to watch the full movie from this screen.

● **Future Screen (Upcoming Feature)**: This screen represents a futuristic feature that is yet to be implemented. It may introduce new functionalities or enhancements to the application, which will be rolled out in future updates. 

**Home Screen :**

● The home screen will be the default display upon opening the application.

● Recent movies will be showcased at the top of the home screen, with automatic transitions to the next movie based on a timer. 

● Various categories of movies will be listed on the home screen, including popular movies, trending movies, movies to watch again, movies in progress, new releases, English movies, blockbuster hits, crime dramas, horror movies, emotional movies, and familiar movies. 

● A separate section will display the top 10 movies. 

● Movies that are available for free will be clearly marked with a "Free" tag next to them. 

**Preview Screen** : 

● On the Preview Screen, the following movie details will be displayed:
- Movie Name 
- Year of Release 
- Certification (e.g., U/A ,A etc.) 
- Duration of the movie
  
● By default, when the user opens the screen, the movie trailer will automatically start playing with the tag "Preview" to indicate that it is just a glimpse or trailer of the movie. 

● To watch the entire movie, users can click the play button, and the movie will start playing in landscape mode by default. 

● The Preview Screen will also provide additional information such as the movie's description, cast members, and genres. 

● Below the movie details, a list of recommended movies, similar to the selected movie, will be displayed. 

● Users will have the option to share the movie with others via email or chat applications. 

● Button interaction is supported for actions such as downloading the movie, adding it to the user's personal list, and rating the movie , in future the functionality will be supported

**Video Player ( Portrait)** 

● The video player will provide options for seeking forward and backward, as well as play and pause functionalities. 

● When the movie trailer is displayed, a "Preview" tag will be prominently shown to indicate that it is only a trailer. 

● Users will be able to conveniently seek forward or backward in the movie using a seek bar. 

● In portrait mode, users will have the option to mute the audio directly. 

● Users will be able to switch to landscape mode, providing a wider viewing experience. 

**Video Player ( landscape)**

● The movie title will be prominently displayed for clarity. 

● On the right side of the screen, a seek bar will allow users to adjust the brightness of the screen. The current brightness percentage and an image representation will be showcased to provide clarity. 

● On the left side of the screen, a seek bar will enable users to adjust the volume. The current volume percentage and an image representation will be displayed for clarity. 

● The forward and backward seek functionality, as well as play and pause buttons, will be available for users' convenience. 

● A seek bar, along with the remaining duration of the movie, will be displayed to help users track the progress and know how much of the movie is left. 

● Users will have the option to lock the screen, preventing accidental touch events until the unlock feature is pressed. 

● Switching to portrait mode will be possible, allowing users to adjust the orientation as per their preference. 

● The functionality for audio and subtitle buttons will be implemented, providing users with options to adjust the audio settings and enable/disable subtitles.


**Other Screens** : 

● The Search, Coming Soon, Downloads, and More screens represent future features and enhancements that will be rolled out in upcoming updates. 

**Tech Stack**

  - Kotlin
  - Constraint Layout
  - Navigation Component
  - Glide and Blur Glide
  - RxJava
  - Retrofit
  - GSON
  - OkHttp
  - ExoPlayer


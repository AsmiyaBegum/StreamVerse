package com.ab.streamverse.model


data class Category(
    val categoryId : Long = 0L,
    val categoryName : String = ""
)

data class StreamDetails (
     var movieName : String = "",
    var thumbnail : String = "",
    var free : Boolean = false,
    var movieKey : String = ""
)

data class VideoStreamCategory (
    val streamName : String = "",
    val streamList : List<StreamDetails>  = listOf(),
    val isTopTen : Boolean = false
)

data class Casting(
    val actors : List<String> = listOf(),
    val directors : List<String> = listOf(),
    val writers : List<String> = listOf(),
)


data class VideoDetails(
    var thumbnail : String = "",
    var movieKey : String = "",
    var trailerKey : String = "",
    var movieName : String = "",
    var year : String = "",
    var certificate : String = "",
    var duration : String = "",
    var description : String = "",
    var cast : Casting = Casting(),
   var genres : List<String> = listOf(),
    var audio : List<String> = listOf(),
    var subtitle : List<String> = listOf(),
    var recommendationKey : String = ""
)
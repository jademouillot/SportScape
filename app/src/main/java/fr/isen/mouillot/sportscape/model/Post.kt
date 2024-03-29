package fr.isen.mouillot.sportscape.model

data class Post(

    val description: String = "",
    var images: List<String>? = null,
    val date : Long = 0,
    val userId: String = "", // Add this line



    )

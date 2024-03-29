package fr.isen.mouillot.sportscape.model

data class Post(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val description: String = "",
    var images: List<String> = listOf(),
    val date: Long = 0,
    var likes: Int = 0,
    val comments: List<Comment> = listOf(),

// Change this line
)
package domain

data class Player(
    val firstName: String,
    val lastName: String,
    val nationality: String,
    val position: Position,
    val price: Double,
    val averageScore: Double,
    val availability: Availability
)

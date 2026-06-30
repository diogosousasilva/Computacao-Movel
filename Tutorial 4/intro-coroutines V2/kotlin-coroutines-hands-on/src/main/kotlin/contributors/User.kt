package contributors

data class User(
    val login: String,
    val contributions: Int
)

data class Repo(
    val name: String
)

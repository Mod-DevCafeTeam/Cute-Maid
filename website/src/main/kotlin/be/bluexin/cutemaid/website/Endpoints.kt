package be.bluexin.cutemaid.website

import be.bluexin.cutemaid.database.User
import io.ktor.locations.Location
import org.jetbrains.exposed.sql.transactions.transaction

@Location("")
class Index

@Location("login")
class Login

@Location("logout")
class Logout

@Location("user/{id}")
data class GetUser(val id: Long) {

    fun getTarget() = transaction { User.findById(id) }

    data class Post(val theme: Themes?)
}

@Location("static")
class Static {
    @Location("avatar")
    class Avatar
}

@Location("api")
class Api

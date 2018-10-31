package be.bluexin.cutemaid.website

import be.bluexin.cutemaid.database.User
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import org.jetbrains.exposed.sql.transactions.transaction

@KtorExperimentalLocationsAPI
@Location("")
class Index

@KtorExperimentalLocationsAPI
@Location("login")
class Login

@KtorExperimentalLocationsAPI
@Location("logout")
class Logout

@KtorExperimentalLocationsAPI
@Location("user/{id}")
data class GetUser(val id: Long) {

    fun getTarget() = transaction { User.findById(id) }

    data class Post(val theme: Themes?)
}

@KtorExperimentalLocationsAPI
@Location("static")
class Static {
    @Location("avatar")
    class Avatar
}

@KtorExperimentalLocationsAPI
@Location("api")
class Api

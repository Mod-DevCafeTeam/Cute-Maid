package be.bluexin.cutemaid.website

import be.bluexin.cutemaid.database.*
import be.bluexin.cutemaid.jacksonMapper
import freemarker.cache.ClassTemplateLoader
import freemarker.template.TemplateExceptionHandler
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.*
import io.ktor.freemarker.FreeMarker
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.isSuccess
import io.ktor.jackson.JacksonConverter
import io.ktor.locations.*
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.sessions.*
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.script
import kotlinx.html.unsafe
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction

internal val logger = KotlinLogging.logger("Cutemaid WebServer")

object WebServer {
    lateinit var settings: WebSettings

    data class WebSettings(
            val port: Int = 8081,
            var discord_id: String = "Discord client ID. Launch args will override this setting.",
            var discord_secret: String = "Discord client secret. Launch args will override this setting."
    )

    fun init() {
        DBManager.tables += UsersTable
        DBManager.tables += SessionStorageDatabase.UserSessionsTable
    }

    @UseExperimental(KtorExperimentalLocationsAPI::class)
    fun startWebserver() {
        embeddedServer(
                Netty,
                watchPaths = listOf("RaidingOrganizer"),
                port = settings.port,
                module = Application::main
        ).start(wait = true)
    }
}

private val discordLogin = OAuthServerSettings.OAuth2ServerSettings(
        name = "discord",
        authorizeUrl = "https://discordapp.com/api/oauth2/authorize",
        accessTokenUrl = "https://discordapp.com/api/oauth2/token",
        requestMethod = HttpMethod.Post,
        clientId = WebServer.settings.discord_id,
        clientSecret = WebServer.settings.discord_secret,
        defaultScopes = listOf("identify", "email")
)

@KtorExperimentalLocationsAPI
private fun Application.installs() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Locations)
//    install(Compression) // TODO: Uncomment once ktorio/ktor#685 is fixed
    install(AutoHeadResponse)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(Index::class.java.classLoader, "templates")
        templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        defaultEncoding = "UTF-8"
    }
    install(StatusPages) {
        status(*HttpStatusCode.allStatusCodes.filter { !it.isSuccess() }.toTypedArray()) {
            call.respond(it, FreeMarkerContent("error.ftl", mapOf("user" to getUser(), "error" to it)))
        }
    }
    install(Sessions) {
        cookie<UserSession>("SESSION", storage = SessionStorageDatabase()) {
            cookie.path = "/"
        }
    }

    val client = HttpClient(Apache)
    environment.monitor.subscribe(ApplicationStopping) {
        client.close()
    }

    install(Authentication) {
        oauth {
            this@oauth.client = client
            providerLookup = { discordLogin }
            urlProvider = { redirectUrl(Login(), false) } // TODO: redirect back to where user was
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(jacksonMapper))
    }
}

@KtorExperimentalLocationsAPI
fun Application.main() {
    installs()

    install(Routing) {
        location<Static> {
            resources("static")
        }

        get<Index> {
            call.respond(FreeMarkerContent("index.ftl", mapOf("user" to call.sessions.get<UserSession>()?.getUser())))
        }

        installLogin()

        post<Logout> {
            call.sessions.clear<UserSession>()
            call.respond(HttpStatusCode.OK)
        }

        installUserEndpoints()
        installEmbeds()
    }
}

@KtorExperimentalLocationsAPI
private fun Routing.installLogin() {
    location<Login> {
        authenticate {
            handle {
                if (call.sessions.get<UserSession>() != null) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()
                    if (principal != null) {
                        val dSession = DiscordSession(principal.accessToken, principal.expiresIn, principal.refreshToken
                                ?: "none")
                        val dUser = dSession.getUser()
                        if (dUser != null) {
                            val dbDUser = transaction {
                                DBDiscordUser.createOrUpdate(dUser.id) {
                                    username = dUser.username
                                    discriminator = dUser.discriminator
                                    avatar = dUser.avatar
                                    email = dUser.email
                                    token = dSession.accessToken
                                    refreshtoken = dSession.refreshToken
                                    locale = dUser.locale
                                }
                            }
                            val user = transaction {
                                User.createIfAbsent(find = { UsersTable.discordUser eq dbDUser.id }, create = {
                                    discordUser = dbDUser
                                    avatar = "discord"
                                    theme = Themes.DARKLY
                                })
                            }
                            call.sessions.set(UserSession(user.id.value))
                            call.respondHtml {
                                body {
                                    script(type = ScriptType.textJavaScript) {
                                        unsafe {
                                            raw("""
                                                window.localStorage.setItem('logged_in', Date.now().toString());
                                                window.close();
                                            """)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@KtorExperimentalLocationsAPI
private fun <T : Any> ApplicationCall.redirectUrl(t: T, secure: Boolean = true): String {
    val hostPort = request.host()!! + request.port().let { port -> if (port == 80) "" else ":$port" }
    val protocol = when {
        secure -> "https"
        else -> "http"
    }
    return "$protocol://$hostPort${application.locations.href(t)}"
}
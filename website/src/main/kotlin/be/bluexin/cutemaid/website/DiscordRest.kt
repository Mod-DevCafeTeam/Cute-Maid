/*
 * Copyright (C) 2018  Arnaud 'Bluexin' Solé
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package be.bluexin.cutemaid.website

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.config
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.util.AttributeKey
import org.apache.http.message.BasicHeader

const val base = "https://discordapp.com/api/v6/"

data class DiscordSession(val accessToken: String, val expiresIn: Long, val refreshToken: String) {
    suspend fun getUser(): DiscordUser? {
        val client = HttpClient(Apache.config {
            followRedirects = true
            customizeClient {
                setDefaultHeaders(listOf(
                        BasicHeader("DiscordUser-Agent", "Test Website Connection (localhost)"),
                        BasicHeader("Authorization", "Bearer $accessToken")
                ))
            }
        }) {
            install(JsonFeature) {
                serializer = JacksonSerializer {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                }
            }
        }
        return try {
            client.get<DiscordUser>("$base/users/@me") {
                setAttributes {
                    put(AttributeKey("token"), accessToken)
                }
            }
        } catch (e: Exception) {
            logger.warn("Catched exception when fetching Discord user.", e)
            null
        }
    }

    suspend fun refresh() {
        logger.debug("Should refresh token. TODO") // TODO
    }
}

data class DiscordUser(
        val id: String,
        val username: String,
        val discriminator: String,
        val avatar: String?,
        val bot: Boolean = false,
        val mfa_enabled: Boolean = false,
        val verified: Boolean = false,
        val email: String? = null,
        val locale: String? = null
) {
    val handle = "$username#$discriminator"
}
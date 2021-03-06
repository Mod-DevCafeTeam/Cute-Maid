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
@file:UseExperimental(KtorExperimentalLocationsAPI::class)

package be.bluexin.cutemaid.website

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import org.jetbrains.exposed.sql.transactions.transaction

fun Routing.installUserEndpoints() {
    get<GetUser> {
        val target = it.getTarget()
        call.respond(target.httpStatusCode, FreeMarkerContent("user.ftl", mapOf(
                "user" to getUser(),
                "target" to target
        ), "e"))
    }

    post<GetUser> {
        val user = getUser()
        val post = call.receive<GetUser.Post>()
        if (user?.id?.value == it.id) {
            transaction {
                if (post.theme != null) user.theme = post.theme
            }
            call.respond(HttpStatusCode.Accepted)
        } else call.respond(HttpStatusCode.Forbidden)
    }
}
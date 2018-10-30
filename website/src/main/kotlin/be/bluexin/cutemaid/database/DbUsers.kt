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

package be.bluexin.cutemaid.database

import be.bluexin.cutemaid.website.Themes
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongIdTable

class User(id: EntityID<Long>) : LongEntity(id) {
    companion object : EagerEntityClass<Long, User>(UsersTable)

    val name get() = discordUser.handle
    val nickname get() = discordUser.handle
    var discordUser by DBDiscordUser.referencedOn(UsersTable.discordUser).cached(this)
    var avatar by UsersTable.avatar
    var theme by UsersTable.theme

    val avatarUrl
        get() = if (avatar == "discord") {
            if (discordUser.avatar != null) "https://cdn.discordapp.com/avatars/${discordUser.id.value}/${discordUser.avatar}.png?size=512"
            else "https://cdn.discordapp.com/avatars/${discordUser.discriminator.toInt() % 5}.png?size=512"
        } else "/static/avatar/$avatar"
}

object UsersTable : LongIdTable() {
    val discordUser = reference("discordUser", DiscordUserTable).uniqueIndex()
    val avatar by varcharNullable(255)
    val theme = enumerationByName("theme", 255, Themes::class)
}
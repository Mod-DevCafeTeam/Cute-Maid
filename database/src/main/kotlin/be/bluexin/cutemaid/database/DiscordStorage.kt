/*
 * Copyright (C) 2018.  Arnaud 'Bluexin' Solé
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

import be.bluexin.cutemaid.escapeHTML
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable

class DBDiscordUser(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, DBDiscordUser>(DiscordUserTable)

    var username
        get() = _username
        set(value) {
            _username = value.escapeHTML()
        }
    private var _username by DiscordUserTable.username
    var cafeNickname
        get() = _cafeNickname
        set(value) {
            _cafeNickname = value?.escapeHTML()
        }
    private var _cafeNickname by DiscordUserTable.cafeNickname
    var discriminator by DiscordUserTable.discriminator
    var avatar by DiscordUserTable.avatar
    var email by DiscordUserTable.email
    var token by DiscordUserTable.token
    var refreshtoken by DiscordUserTable.refreshToken

    val handle get() = "$username#$discriminator"
}

object DiscordUserTable : IdTable<String>("") {
    override val id = varchar("id", 255).primaryKey().entityId()

    val username by varchar(255)
    val cafeNickname by varcharNullable(255)
    val discriminator by varchar(255) // Shouldn't exceed 4 digits, but we never know when they may change that
    val avatar by varcharNullable(255)
    val email by varcharNullable(255)
    val token by varchar(255)
    val refreshToken by varchar(255)
}
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

import io.ktor.sessions.SessionStorage
import io.ktor.util.cio.toByteArray
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.io.ByteReadChannel
import kotlinx.coroutines.experimental.io.ByteWriteChannel
import kotlinx.coroutines.experimental.io.writer
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.sql.deleteIgnoreWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class SessionStorageDatabase : SessionStorage {
    override suspend fun invalidate(id: String) {
        transaction {
            UserSessionsTable.deleteIgnoreWhere { UserSessionsTable.id eq id }
        }
    }

    override suspend fun <R> read(id: String, consumer: suspend (ByteReadChannel) -> R): R {
        return transaction {
            UserSession.findById(id)?.apply { lastAccess = DateTime.now() }
        }?.let { consumer(ByteReadChannel(it.data.toByteArray())) }
                ?: throw NoSuchElementException("Session $id not found")
    }

    override suspend fun write(id: String, provider: suspend (ByteWriteChannel) -> Unit) {
        val data = String(writer(Dispatchers.Unconfined, autoFlush = true) {
            provider(channel)
        }.channel.toByteArray())
        transaction {
            UserSession.createOrUpdate(id) {
                this.data = data
                this.lastAccess = DateTime.now()
            }
        }
    }

    class UserSession(id: EntityID<String>) : Entity<String>(id) {
        companion object : EntityClass<String, UserSession>(UserSessionsTable)

        var data by UserSessionsTable.data
        var lastAccess by UserSessionsTable.lastAccess
    }

    object UserSessionsTable : IdTable<String>("") {
        override val id = varchar("id", 255).primaryKey().entityId()
        val data = text("data")
        val lastAccess = datetime("lastAccess")
    }
}
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

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.SQLException
import kotlin.system.exitProcess

object DBManager {
    internal val logger = KotlinLogging.logger {  }
    lateinit var settings : DatabaseSettings

    data class DatabaseSettings(
            val dburl: String = "database url (in the form of jdbc:mysql://ip:port/database)",
            val dbuser: String = "database username",
            val dbpassword: String = "database password"
    )

    fun init() {
        logger.info("Setting up db connection...")
        val db = Database.connect(settings.dburl, "org.mariadb.jdbc.Driver", user = settings.dbuser, password = settings.dbpassword)
        try {
            logger.info("Connected using ${db.vendor} database on version ${db.version}")
            transaction {
                SchemaUtils.createMissingTablesAndColumns(

                )
            }
        } catch (e: SQLException) {
            logger.error("Couldn't connect to database.", e)
            exitProcess(1)
        }
    }
}
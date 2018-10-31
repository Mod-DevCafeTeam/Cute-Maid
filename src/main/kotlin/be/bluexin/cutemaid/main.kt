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

package be.bluexin.cutemaid

import be.bluexin.cutemaid.database.DBManager
import be.bluexin.cutemaid.website.WebServer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

val logger = KotlinLogging.logger("Cutemaid Main")

fun main(args: Array<String>) {
    SettingsManager.settings
    WebServer.init()
    DBManager.init()

    runBlocking {
        launch {
            WebServer.startWebserver()
        }
    }
}
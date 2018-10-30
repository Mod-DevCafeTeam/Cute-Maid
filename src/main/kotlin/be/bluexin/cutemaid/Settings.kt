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
import java.io.File
import kotlin.system.exitProcess

object SettingsManager {


    val settings by lazy {
        val settingsFile = File("settings.json")
        if (!settingsFile.exists()) {
            Settings().writeJson(settingsFile)
            logger.warn("Default config file was generated. Please edit with the correct info.")
            exitProcess(0)
        } else {
            try {
                readJson<Settings>(settingsFile).also {
                    // To make sure we get updated config
                    it.writeJson(settingsFile)
                    it.apply()
                }
            } catch (e: Exception) {
                logger.error("Config file couldn't be parsed. Please consider deleting it to regenerate it.")
                exitProcess(1)
            }
        }
    }

    data class Settings(
            val database: DBManager.DatabaseSettings = DBManager.DatabaseSettings(),
            val webserver: WebServer.WebSettings = WebServer.WebSettings()
    ) {
        fun apply() {
            DBManager.settings = this.database
            WebServer.settings = this.webserver
        }
    }
}
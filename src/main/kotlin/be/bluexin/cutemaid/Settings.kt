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
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File
import kotlin.system.exitProcess

object SettingsManager {

    val jacksonMapper = ObjectMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .enable(SerializationFeature.INDENT_OUTPUT)!!

    val settings by lazy {
        val settingsFile = File("settings.json")
        if (!settingsFile.exists()) {
            jacksonMapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, Settings())
            println("Default config file was generated. Please edit with the correct info.")
            exitProcess(0)
        } else {
            try {
                jacksonMapper.readValue<Settings>(settingsFile).also {
                    // To make sure we get updated configexit
                    jacksonMapper.writerWithDefaultPrettyPrinter().writeValue(settingsFile, it)
                    DBManager.settings = it.database
                }
            } catch (e: Exception) {
                println("Config file couldn't be parsed. Please consider deleting it to regenerate it.")
                exitProcess(1)
            }
        }
    }

    data class Settings(
            val database: DBManager.DatabaseSettings = DBManager.DatabaseSettings()
    )
}
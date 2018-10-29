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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

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

fun String.escapeHTML(): String {
    if (this.isEmpty()) return this

    return buildString(length) {
        for (idx in 0 until this@escapeHTML.length) {
            val ch = this@escapeHTML[idx]
            when (ch) {
                '\"' -> append("&quot")
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                ' ' -> append("&#x20;")
                '!' -> append("&#x21;")
                '$' -> append("&#x24;")
                '%' -> append("&#x25;")
                '\'' -> append("&#x27;")
                '/' -> append("&#x2F;")
                '+' -> append("&#x2b;")
                ',' -> append("&#x2c;")
                '=' -> append("&#x3d;")
                '?' -> append("&#x3f;")
                '@' -> append("&#x40;")
                '[' -> append("&#x5b;")
                ']' -> append("&#x5d;")
                '`' -> append("&#x60;")
                '{' -> append("&#x7b;")
                '}' -> append("&#x7d;")
                else -> append(ch)
            }
        }
    }
}

val jacksonMapper = ObjectMapper()
        .registerKotlinModule()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(SerializationFeature.INDENT_OUTPUT)!!

fun Any.writeJson(file: File) {
    jacksonMapper.writerWithDefaultPrettyPrinter().writeValue(file, this)
}

inline fun <reified T> readJson(file: File): T = jacksonMapper.readValue(file)

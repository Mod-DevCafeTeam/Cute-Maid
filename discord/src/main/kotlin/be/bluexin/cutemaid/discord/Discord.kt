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

package be.bluexin.cutemaid.discord

import mu.KotlinLogging
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.events.ReadyEvent
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

internal val logger = KotlinLogging.logger {  }

object Discord : ListenerAdapter() {
    lateinit var settings: DiscordSettings
    lateinit var jda: JDA

    data class DiscordSettings(
            var token: String? = "Bot token (is overwritten by command-line token argument)",
            val watchingGuildId: Long,
            val repUpEmoteId: Long,
            val repDownEmoteId: Long
    )

    fun init() {
        val jda = JDABuilder(settings.token).addEventListener(this).build()
    }

    override fun onReady(event: ReadyEvent) = logger.info("Logged in as ${event.jda.selfUser.name}")

    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent?) {
        super.onGuildMessageReactionAdd(event)
    }


}
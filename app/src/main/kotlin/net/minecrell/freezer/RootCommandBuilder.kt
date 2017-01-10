/*
 * Freezer
 * Copyright (C) 2016 Minecrell
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.freezer

internal fun buildRootCommand(apps: Array<out App>, enable: Boolean): Process {
    return ProcessBuilder(listOf("su", "root", buildCommand(apps, enable))).start()
}

private fun buildCommand(apps: Array<out App>, enable: Boolean): String {
    apps.singleOrNull()?.let { return buildSingleCommand(it, enable) }
    return buildCombinedCommand(apps, enable)
}

private fun buildSingleCommand(app: App, enable: Boolean): String {
    return if (enable) {
        ENABLE_APP(app)
    } else {
        DISABLE_APP(app)
    }
}

private fun buildCombinedCommand(apps: Array<out App>, enable: Boolean): String {
    val builder = StringBuilder("sh -c '")

    if (enable) {
        apps.joinTo(builder, separator = "; ", transform = ENABLE_APP)
    } else {
        apps.joinTo(builder, separator = "; ", transform = DISABLE_APP)
    }

    return builder.append('\'').toString()
}

private val ENABLE_APP = { app: App -> "pm enable ${app.data.packageName}" }
private val DISABLE_APP = { app: App -> "pm disable ${app.data.packageName}" }

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

internal fun buildRootCommand(apps: Array<out App>, enable: Boolean): ProcessBuilder {
    return ProcessBuilder(listOf("su", "-c", buildCommand(apps, enable)))
}

private fun buildCommand(apps: Array<out App>, enable: Boolean): String {
    val transform = if (enable) ENABLE_APP else DISABLE_APP
    return apps.joinToString(separator = "; ", transform = transform)
}

private val ENABLE_APP = { app: App -> "pm enable ${app.data.packageName}" }
private val DISABLE_APP = { app: App -> "pm disable ${app.data.packageName}" }

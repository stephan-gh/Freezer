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

import android.content.pm.ApplicationInfo

internal fun freezeApps(apps: List<ApplicationInfo>, state: Boolean) {
    val process = ProcessBuilder(listOf("su", "root", buildCommand(apps, state))).start()
    // TODO: Display progress + check result
    process.waitFor()
}

private fun buildCommand(apps: List<ApplicationInfo>, state: Boolean): String {
    apps.singleOrNull()?.let { return buildSingleCommand(it, state) }
    return buildCombinedCommand(apps, state)
}

private fun buildSingleCommand(app: ApplicationInfo, state: Boolean): String {
    return if (state) {
        DISABLE_APP(app)
    } else {
        ENABLE_APP(app)
    }
}

private fun buildCombinedCommand(apps: List<ApplicationInfo>, state: Boolean): String {
    val builder = StringBuilder("sh -c '")

    if (state) {
        apps.joinTo(builder, separator = "; ", transform = DISABLE_APP)
    } else {
        apps.joinTo(builder, separator = "; ", transform = ENABLE_APP)
    }

    return builder.append('\'').toString()
}

private val ENABLE_APP = { app: ApplicationInfo -> "pm enable ${app.packageName}" }
private val DISABLE_APP = { app: ApplicationInfo -> "pm disable ${app.packageName}" }

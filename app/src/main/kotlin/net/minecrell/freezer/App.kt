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

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri

internal data class App(internal val name: String, internal val data: ApplicationInfo) : Comparable<App> {

    override fun compareTo(other: App) = name.compareTo(other.name, ignoreCase = true)

    internal val uri: Uri
        get() = Uri.parse("package:${data.packageName}")

    internal fun createIntent(action: String) = Intent(action, uri)

}

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

internal class UninstallApp(fragment: AppListFragment) : AppAction.Context(fragment) {

    override val progressRes: Int
        get() = R.string.uninstall_progress

    override val doneRes: Int
        get() = R.plurals.uninstall_done

    private var iterator: Iterator<App>? = null

    override fun run(apps: List<App>) {
        this.iterator = apps.iterator()
        uninstallApp()
    }

    private fun uninstallApp() {
        val iterator = this.iterator!!
        if (!iterator.hasNext()) {
            done()
            return
        }

        val app = iterator.next()
        fragment.startActivityForResult(app.createIntent(Intent.ACTION_UNINSTALL_PACKAGE),
                AppListFragment.REQUEST_UNINSTALL)
    }

    override fun next() {
        uninstallApp()
        super.next()
    }

}

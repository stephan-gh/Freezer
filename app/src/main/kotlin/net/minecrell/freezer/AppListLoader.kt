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
import android.content.pm.PackageManager
import android.os.AsyncTask
import java.util.ArrayList

internal class AppListLoader(private val activity: FreezerActivity,
                             private val pm: PackageManager) : AsyncTask<Nothing, Nothing, Array<List<App>>>() {

    @Suppress("UNCHECKED_CAST")
    override fun doInBackground(vararg params: Nothing): Array<List<App>> {
        val apps = pm.getInstalledApplications(0).mapToSortedArray { data -> App(data.loadLabel(pm).toString(), data) }

        val result = Array(AppAction.COUNT, { ArrayList<App>() })

        for (app in apps) {
            val data = app.data

            when {
                !data.enabled -> result[AppAction.ENABLE]
                ApplicationInfo.FLAG_SYSTEM !in data.flags -> result[AppAction.UNINSTALL]
                else -> result[AppAction.DISABLE]
            }.add(app)
        }

        return result as Array<List<App>>
    }

    override fun onCancelled() {
        activity.stopRefresh()

        for (adapter in activity.adapters) {
            adapter.setRefreshing(false)
        }
    }

    override fun onPostExecute(result: Array<List<App>>) {
        activity.stopRefresh()

        mapArrays(activity.adapters, result) { adapter, apps ->
            adapter.update(pm, apps)
            adapter.setRefreshing(false)
        }
    }

}

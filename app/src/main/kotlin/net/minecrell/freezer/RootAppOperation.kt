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

import android.content.DialogInterface
import android.os.AsyncTask
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog

internal abstract class RootAppOperation(fragment: AppListFragment) : AppAction.Context(fragment) {

    internal class Enable(fragment: AppListFragment) : RootAppOperation(fragment) {

        override val progressRes: Int
            get() = R.string.enable_progress

        override val doneRes: Int
            get() = R.plurals.enable_done

        override val enable: Boolean
            get() = true

    }

    internal class Disable(fragment: AppListFragment) : RootAppOperation(fragment),
            MaterialDialog.SingleButtonCallback, DialogInterface.OnCancelListener {

        override val progressRes: Int
            get() = R.string.disable_progress

        override val doneRes: Int
            get() = R.plurals.disable_done

        override val enable: Boolean
            get() = false


        override fun start() {
            MaterialDialog.Builder(fragment.context)
                    .content(R.string.disable_apps_warning)
                    .positiveText(R.string.disable_apps_positive)
                    .onPositive(this)
                    .negativeText(R.string.disable_apps_negative)
                    .cancelListener(this)
                    .show()
        }

        override fun onClick(dialog: MaterialDialog, which: DialogAction) {
            super.start()
        }

        override fun onCancel(dialog: DialogInterface?) {
            done()
        }

    }

    protected abstract val enable: Boolean

    override fun run(apps: List<App>) {
        Task(this).execute(*apps.toTypedArray())
    }

    private class Task(private val operation: RootAppOperation) : AsyncTask<App, App, Unit>() {

        override fun doInBackground(vararg apps: App) {
            val process = buildRootCommand(apps, operation.enable)
                    .redirectErrorStream(true)
                    .start()
            val appMap = apps.associateBy { app -> app.data.packageName }

            process.inputStream.bufferedReader().use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    if (line.startsWith("Package ")) {
                        val end = line.indexOf(' ', 8)
                        val packageName = if (end != -1) {
                            line.substring(8, end)
                        } else {
                            line.substring(8)
                        }

                        val app = appMap[packageName]
                        if (app != null) {
                            publishProgress(app)
                        }
                    }

                    line = reader.readLine()
                }
            }

            process.waitFor()
        }

        override fun onProgressUpdate(vararg values: App?) {
            operation.next()
        }

        override fun onPostExecute(result: Unit?) {
            operation.done()
        }

    }

}

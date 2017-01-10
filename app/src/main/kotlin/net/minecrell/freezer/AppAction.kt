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

import com.afollestad.materialdialogs.MaterialDialog

internal enum class AppAction(internal val nameRes: Int, private val progressRes: Int) {

    UNINSTALL(R.string.uninstall, R.string.uninstall_progress) {
        override fun createContext(fragment: AppListFragment) = UninstallApp(fragment)
    },
    DISABLE(R.string.disable, R.string.disable_progress) {
        override fun createContext(fragment: AppListFragment) = RootAppOperation.Disable(fragment)
    },
    ENABLE(R.string.enable, R.string.enable_progress) {
        override fun createContext(fragment: AppListFragment) = RootAppOperation.Enable(fragment)
    };

    internal abstract fun createContext(fragment: AppListFragment): Context

    internal abstract class Context(protected val fragment: AppListFragment) {

        protected abstract val progressRes: Int
        protected abstract val doneRes: Int

        protected var dialog: MaterialDialog? = null

        internal open fun start() {
            val apps = fragment.selectedApps
            dialog = MaterialDialog.Builder(fragment.context)
                    .title(progressRes)
                    .progress(false, apps.size, true)
                    .cancelable(false)
                    .show()

            run(apps)
        }

        protected abstract fun run(apps: List<App>)

        internal fun done() {
            val dialog = dialog
            if (dialog != null) {
                val count = dialog.currentProgress
                val message = if (count > 0) {
                    fragment.resources.getQuantityString(doneRes, count, count)
                } else {
                    null
                }

                dialog.cancel()
                this.dialog = null

                fragment.finishAction(message)
            } else {
                fragment.finishAction(null)
            }
        }

        internal open fun next() {
            dialog?.incrementProgress(1)
        }

    }

    internal companion object {

        private val values = AppAction.values()

        internal const val COUNT = 3
        internal operator fun get(ordinal: Int) = values[ordinal]

    }


}

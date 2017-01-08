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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerView
import com.afollestad.materialcab.MaterialCab

internal class FreezerFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
        AppListAdapter.Listener, MaterialCab.Callback {

    private val state: Boolean
        get() = arguments.getBoolean(ARG_FREEZE, true)

    private lateinit var activity: FreezerActivity

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: DragSelectRecyclerView
    private lateinit var adapter: AppListAdapter

    private lateinit var cab: MaterialCab
    private var finished = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_freezer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.refreshLayout = view.findViewById(R.id.refresh_layout) as SwipeRefreshLayout
        refreshLayout.setOnRefreshListener(this)

        this.recyclerView = view.findViewById(R.id.recycler_view) as DragSelectRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        val pm = context.packageManager
        this.adapter = AppListAdapter(pm, collectApps(pm), this)
        recyclerView.setAdapter(this.adapter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context as FreezerActivity
        this.cab = context.cab
    }

    private fun collectApps(pm: PackageManager) = pm.getInstalledApplications(0).filter { it.enabled == this.state }

    internal fun refresh() {
        this.refreshLayout.isRefreshing = true
        onRefresh()
    }

    override fun onRefresh() {
        this.adapter.updateApps(collectApps(context.packageManager))
        this.refreshLayout.isRefreshing = false
    }

    override fun onClick(index: Int) {
        this.adapter.toggleSelected(index)
    }

    override fun onLongClick(index: Int) {
        this.recyclerView.setDragSelectActive(true, index)
    }

    override fun onDragSelectionChanged(count: Int) {
        if (count > 0) {
            cab.setTitle(resources.getQuantityString(R.plurals.app_selection, count, count))

            if (!this.cab.isActive) {
                this.finished = false
                this.cab.start(this)
            }
        } else if (!this.finished) {
            this.cab.finish()
        }
    }

    override fun onCabCreated(cab: MaterialCab, menu: Menu) = true

    override fun onCabItemClicked(item: MenuItem): Boolean {
        if (item.itemId == R.id.apply) {
            val apps = this.adapter.selectedIndices.map { this.adapter[it] }
            this.adapter.clearSelected()

            freezeApps(apps, this.state)
            this.activity.refresh()
        }

        return true
    }

    override fun onCabFinished(cab: MaterialCab): Boolean {
        if (!this.finished) {
            this.finished = true
            this.adapter.clearSelected()
        }
        return true
    }

    override fun showAppDetails(index: Int) {
        val app = this.adapter[index]

        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${app.packageName}")
        startActivity(intent)
    }

    internal companion object {

        internal const val ARG_FREEZE = "freeze"

        internal fun create(freeze: Boolean) = FreezerFragment()
                .apply { arguments = Bundle().apply { putBoolean(ARG_FREEZE, freeze) } }

    }

}

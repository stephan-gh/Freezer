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
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
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

internal class AppListFragment : Fragment(), AppListAdapter.Handler, MaterialCab.Callback {

    private lateinit var action: AppAction

    private lateinit var activity: FreezerActivity
    private lateinit var adapter: AppListAdapter

    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: DragSelectRecyclerView

    private var finished = false
    private var currentAction: AppAction.Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        this.action = arguments.getSerializable(ARG_ACTION) as AppAction

        this.activity = context as FreezerActivity
        this.adapter = context.adapters[action]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                    savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        this.refreshLayout = view.findViewById(R.id.refresh_layout) as SwipeRefreshLayout
        setRefreshing(this.activity.refreshing)
        refreshLayout.setOnRefreshListener(activity)

        this.recyclerView = view.findViewById(R.id.recycler_view) as DragSelectRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setAdapter(adapter)

        adapter.setHandler(this)
    }

    internal val selectedApps: List<App>
        get() = adapter.selectedIndices.map { this.adapter[it]!! }

    internal fun finishAction(message: String?) {
        if (message != null) {
            Snackbar.make(refreshLayout, message, Snackbar.LENGTH_LONG).show()
        }

        this.currentAction = null
        this.adapter.clearSelected()
        this.activity.onRefresh()
    }

    override fun setRefreshing(state: Boolean) {
        refreshLayout.isRefreshing = state
    }

    override fun onClick(index: Int) {
        adapter.toggleSelected(index)
    }

    override fun onLongClick(index: Int) {
        recyclerView.setDragSelectActive(true, index)
    }

    override fun onDragSelectionChanged(count: Int) {
        if (count > 0) {
            activity.cab.setTitle(resources.getQuantityString(R.plurals.app_selection, count, count))

            if (!activity.cab.isActive) {
                this.finished = false
                activity.cab.start(this)
            }
        } else if (!finished) {
            this.finished = true
            activity.cab.finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_UNINSTALL) {
            currentAction!!.next()
        }
    }

    override fun onCabCreated(cab: MaterialCab, menu: Menu) = true

    override fun onCabItemClicked(item: MenuItem): Boolean {
        if (item.itemId == R.id.apply && this.currentAction == null && adapter.selectedCount > 0) {
            this.currentAction = action.createContext(this).apply { start() }
        }

        return true
    }

    override fun onCabFinished(cab: MaterialCab?): Boolean {
        if (!finished) {
            this.finished = true
            adapter.clearSelected()
        }

        return true
    }

    override fun showAppDetails(index: Int) {
        val app = adapter[index]!!
        startActivity(app.createIntent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS))
    }

    internal companion object {

        internal const val ARG_ACTION = "action"
        internal const val REQUEST_UNINSTALL = 0

        internal fun create(action: AppAction): AppListFragment {
            val fragment = AppListFragment()

            val bundle = Bundle()
            bundle.putSerializable(AppListFragment.ARG_ACTION, action)
            fragment.arguments = bundle

            return fragment
        }

    }

}

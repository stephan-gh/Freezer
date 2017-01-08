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
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter

internal class AppListAdapter(private val pm: PackageManager, apps: List<ApplicationInfo>,
                              private val listener: Listener)
    : DragSelectRecyclerViewAdapter<AppListAdapter.ViewHolder>() {

    internal interface Listener : SelectionListener {
        fun onClick(index: Int)
        fun onLongClick(index: Int)

        fun showAppDetails(index: Int)
    }

    init {
        setSelectionListener(listener)
    }

    private var apps: List<App> = prepareApps(apps)

    internal operator fun get(index: Int): ApplicationInfo = apps[index].app

    private fun prepareApps(apps: List<ApplicationInfo>): List<App> {
        return apps.map { app -> App(app.loadLabel(pm).toString(), app) }.sorted().toList()
    }

    internal fun updateApps(apps: List<ApplicationInfo>) {
        this.apps = prepareApps(apps)
        notifyDataSetChanged()
    }

    override fun getItemCount() = apps.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false), listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val (name, app) = apps[position]
        holder.title.text = name

        val selected = isIndexSelected(position)
        holder.itemView.isSelected = selected

        if (selected) {
            holder.icon.setImageResource(R.drawable.ic_check_circle_primary)
        } else {
            holder.icon.setImageDrawable(app.loadIcon(pm))
        }
    }

    private data class App(val name: String, val app: ApplicationInfo) : Comparable<App> {
        override fun compareTo(other: App) = name.compareTo(other.name, ignoreCase = true)
    }

    internal class ViewHolder(view: View, private val listener: Listener) : RecyclerView.ViewHolder(view),
            View.OnClickListener, View.OnLongClickListener {

        internal val title: TextView = view.findViewById(android.R.id.title) as TextView
        internal val icon: ImageView = view.findViewById(android.R.id.icon) as ImageView
        internal val moreInfo: View = view.findViewById(R.id.more_info)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            moreInfo.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === moreInfo) {
                listener.showAppDetails(adapterPosition)
            } else {
                listener.onClick(adapterPosition)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            listener.onLongClick(adapterPosition)
            return true
        }

    }

}

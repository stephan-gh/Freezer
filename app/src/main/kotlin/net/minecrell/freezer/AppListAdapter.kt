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

import android.content.pm.PackageManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.dragselectrecyclerview.DragSelectRecyclerViewAdapter

internal class AppListAdapter : DragSelectRecyclerViewAdapter<AppListAdapter.ViewHolder>() {

    internal interface Handler : SelectionListener {
        fun onClick(index: Int)
        fun onLongClick(index: Int)

        fun showAppDetails(index: Int)

        fun setRefreshing(state: Boolean)
    }

    private var pm: PackageManager? = null
    private var apps: List<App>? = null
    private var handler: Handler? = null

    internal fun setHandler(handler: Handler) {
        this.handler = handler
        setSelectionListener(handler)
    }

    internal fun setRefreshing(state: Boolean) {
        handler?.setRefreshing(state)
    }

    internal operator fun get(index: Int): App? = apps?.get(index)

    internal fun update(pm: PackageManager, apps: List<App>) {
        this.pm = pm
        this.apps = apps
        notifyDataSetChanged()
    }

    override fun getItemCount() = apps?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false), handler!!)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val (name, app) = apps!![position]
        holder.title.text = name

        val selected = isIndexSelected(position)
        holder.itemView.isSelected = selected

        if (selected) {
            holder.icon.setImageResource(R.drawable.ic_check_circle_primary)
        } else {
            holder.icon.setImageDrawable(app.loadIcon(pm))
        }
    }

    internal class ViewHolder(view: View, private val handler: Handler) : RecyclerView.ViewHolder(view),
            View.OnClickListener, View.OnLongClickListener {

        internal val title: TextView = view.findViewById(android.R.id.title) as TextView
        internal val icon: ImageView = view.findViewById(android.R.id.icon) as ImageView
        internal val appDetails: View = view.findViewById(R.id.app_details)

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            appDetails.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v === appDetails) {
                handler.showAppDetails(adapterPosition)
            } else {
                handler.onClick(adapterPosition)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            handler.onLongClick(adapterPosition)
            return true
        }

    }

}

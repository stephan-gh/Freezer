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

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.afollestad.materialcab.MaterialCab

class FreezerActivity : AppCompatActivity(), ViewPager.OnPageChangeListener, SwipeRefreshLayout.OnRefreshListener {

    internal val adapters = Array(AppAction.COUNT, { AppListAdapter() })

    internal lateinit var cab: MaterialCab
        private set

    internal var refreshing = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freezer)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        this.cab = MaterialCab(this, R.id.cab_stub)

        val pager = findViewById(R.id.view_pager) as ViewPager
        pager.addOnPageChangeListener(this)
        pager.adapter = FreezerPagerAdapter(this, supportFragmentManager)
        pager.offscreenPageLimit = AppAction.COUNT - 1

        val tabLayout = findViewById(R.id.tab_layout) as TabLayout
        tabLayout.setupWithViewPager(pager)

        onRefresh()
    }

    override fun onRefresh() {
        if (this.refreshing) {
            return
        }

        this.refreshing = true

        for (adapter in adapters) {
            adapter.setRefreshing(true)
        }

        AppListLoader(this, packageManager).execute()
    }

    internal fun stopRefresh() {
        this.refreshing = false
    }

    override fun onPageSelected(position: Int) {
        cancelContextActionBar()
    }

    override fun onBackPressed() {
        if (!cancelContextActionBar()) {
            super.onBackPressed()
        }
    }

    private fun cancelContextActionBar(): Boolean {
        if (!cab.isActive) {
            return false
        }

        cab.finish()
        return true
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    override fun onPageScrollStateChanged(state: Int) {}

}

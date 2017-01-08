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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

internal class FreezerPagerAdapter(val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getCount() = 2

    override fun getPageTitle(position: Int): CharSequence {
        return context.getString(when(position) {
            0 -> R.string.freeze
            1 -> R.string.unfreeze
            else -> invalidPosition(position)
        })
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FreezerFragment.create(true)
            1 -> FreezerFragment.create(false)
            else -> invalidPosition(position)
        }
    }

    private fun invalidPosition(position: Int): Nothing = throw AssertionError("Invalid position: $position")

}

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

@file:Suppress("NOTHING_TO_INLINE")
package net.minecrell.freezer

internal inline fun <T, reified R : Comparable<R>> List<T>.mapToSortedArray(transform: (T) -> R): Array<R> {
    return Array(size, { i -> transform(this[i]) }).apply { sort() }
}

internal inline fun <A, B> mapArrays(a: Array<A>, b: Array<B>, func: (A, B) -> Unit) {
    for (i in a.indices) {
        func(a[i], b[i])
    }
}

internal inline operator fun <T> Array<T>.get(enum: Enum<*>): T = this[enum.ordinal]

internal inline operator fun Int.contains(bitmask: Int) = (this and bitmask) != 0

package com.exallium.testinglayers.view

import android.support.v7.util.DiffUtil
import com.exallium.testinglayers.presentation.Diffable

class DiffCallback<in T : Diffable<T>>(
        private val oldItems: List<T>,
        private val newItems: List<T>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldItems[oldItemPosition].sameAs(newItems[newItemPosition])

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldItems[oldItemPosition].sameContentsAs(newItems[newItemPosition])
}
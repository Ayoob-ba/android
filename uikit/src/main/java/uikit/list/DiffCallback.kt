package uikit.list

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class DiffCallback: DiffUtil.ItemCallback<BaseListItem>() {

    companion object {
        fun create() = DiffCallback()
    }

    override fun areItemsTheSame(oldItem: BaseListItem, newItem: BaseListItem): Boolean {
        return oldItem.type == newItem.type
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BaseListItem, newItem: BaseListItem): Boolean {
        return newItem == oldItem
    }
}
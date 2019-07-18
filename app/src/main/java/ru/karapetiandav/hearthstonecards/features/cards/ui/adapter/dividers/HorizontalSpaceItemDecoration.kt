package ru.karapetiandav.hearthstonecards.features.cards.ui.adapter.dividers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.karapetiandav.hearthstonecards.extensions.toPx


class HorizontalSpaceItemDecoration(private val space: Int, private val spaceStart: Int, private val spaceEnd: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.left = spaceStart.toPx
                outRect.right = space.toPx
            }
            parent.adapter?.itemCount?.minus(1) -> outRect.right = spaceEnd.toPx
            else -> outRect.right = space.toPx
        }
    }


}
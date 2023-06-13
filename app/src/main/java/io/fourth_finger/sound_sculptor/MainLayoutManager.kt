package io.fourth_finger.sound_sculptor

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State


class MainLayoutManager(context: Context) : LinearLayoutManager(context) {

    private var firstRowItemCount = 0

    private var totalHeight = 0
    private var totalWidth = 0

    override fun onAdapterChanged(
        oldAdapter: RecyclerView.Adapter<*>?,
        newAdapter: RecyclerView.Adapter<*>?
    ) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        if (newAdapter is MainRecyclerViewAdapter) {
            firstRowItemCount = newAdapter.getFirstRowItemCount()
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    private var totalDx = 0

    override fun computeHorizontalScrollOffset(state: State): Int {
        return totalDx
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        totalWidth = getTotalWidth()

        var scroll = false
        var actualDx = 0

        if(dx > 0) {
            val lastViewRight = getChildAt(childCount - 1)?.let { getDecoratedRight(it) } ?: 0
            val lastFirstRowViewRight = getChildAt(firstRowItemCount - 1)?.let { getDecoratedRight(it) } ?: 0
            if (lastViewRight >= width || lastFirstRowViewRight >= width) {
                var maxScroll = totalWidth - totalDx - width
                if (maxScroll < 0) {
                    maxScroll = 0
                }
                actualDx = minOf(dx, maxScroll)
                scroll = true
            }
        } else if(dx < 0) {
            val firstViewLeft = getChildAt(0)?.let { getDecoratedLeft(it) } ?: 0
            if (firstViewLeft <= 0) {
                actualDx = if(totalDx < dx){
                    -totalDx
                } else {
                    maxOf(dx, -totalDx)
                }
                scroll = true
            }
        }

        if (scroll){
            offsetChildrenHorizontal(-actualDx)
        }

        totalDx += actualDx
        return actualDx
    }

    private fun getTotalWidth(): Int {
        var totalWidth1 = 0
        for(i in 0 until firstRowItemCount){
            val view = getChildAt(i)
            totalWidth1 += view?.width?:0
        }

        var totalWidth2 = 0
        for(i in 0 until firstRowItemCount){
            val view = getChildAt(i)
            totalWidth2 += view?.width?:0
        }
        return maxOf(totalWidth1, totalWidth2)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }


    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ) {
        layout(recycler, state)
    }

    private fun layout(recycler: Recycler, state: State) {
        val heightUsed =  height / 2
        var height = 0
        var width = 0
        detachAndScrapAttachedViews(recycler)

        // First row
        for (position in 0 until firstRowItemCount) {

            val rootView = recycler.getViewForPosition(position)
            addView(rootView)

            val layoutParams = rootView.layoutParams
            layoutParams.height = heightUsed
            rootView.layoutParams = layoutParams

            measureChildWithMargins(rootView, 0, 0)
            layoutDecoratedWithMargins(
                rootView,
                width + paddingLeft,
                0,
                paddingLeft + width + rootView.measuredWidth,
                rootView.measuredHeight
            )
            width += rootView.measuredWidth
            totalWidth += rootView.measuredWidth
            height = maxOf(heightUsed, rootView.measuredHeight)
        }

        // Second row
        width = 0
        var secondRowHeight = 0
        for (position in firstRowItemCount until state.itemCount) {

            val rootView = recycler.getViewForPosition(position)
            addView(rootView)

            val layoutParams = rootView.layoutParams
            layoutParams.height = heightUsed
            rootView.layoutParams = layoutParams

            measureChildWithMargins(rootView, 0, 0)
            layoutDecoratedWithMargins(
                rootView,
                width,
                height,
                paddingLeft + width + rootView.measuredWidth,
                height + rootView.measuredHeight
            )
            width += rootView.measuredWidth
            secondRowHeight = maxOf(heightUsed, rootView.measuredHeight)
        }
        totalWidth = maxOf(totalWidth, width)

        recycler.scrapList.forEach {
            recycler.recycleView(it.itemView)
        }

        totalHeight = height + secondRowHeight
    }

}
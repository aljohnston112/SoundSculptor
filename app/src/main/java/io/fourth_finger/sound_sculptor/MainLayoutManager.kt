package io.fourth_finger.sound_sculptor

import android.content.Context
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.State

/**
 * For laying out an envelope with two rows of envelope segments.
 * One row is for the amplitude envelope segments and
 * the other is for the frequency envelope segments.
 */
class MainLayoutManager(context: Context) : LinearLayoutManager(context) {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    private var totalDx = 0

    override fun computeHorizontalScrollOffset(state: State): Int {
        return totalDx
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: State): Int {
        val totalWidth = getTotalWidth()

        var scroll = false
        var actualDx = 0

        if(dx > 0) {
            val lastViewRight = getChildAt(childCount - 1)?.let { getDecoratedRight(it) } ?: 0
            val lastFirstRowViewRight = getChildAt(getNumAmplitudeEnvelopeSegments())?.let { getDecoratedRight(it) } ?: 0
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
        for(i in 0 until getFirstRowItemCount()){
            val view = getChildAt(i)
            totalWidth1 += view?.width?:0
        }

        var totalWidth2 = 0
        for(i in 0 until getFirstRowItemCount()){
            val view = getChildAt(i)
            totalWidth2 += view?.width?:0
        }
        return maxOf(totalWidth1, totalWidth2)
    }

    /**
     * @return The number of views in the first row.
     */
    private fun getFirstRowItemCount(): Int {
        return  getNumAmplitudeEnvelopeSegments() + 1
    }

    override fun onLayoutChildren(
        recycler: RecyclerView.Recycler,
        state: State
    ) {
        layout(recycler, state)
    }

    /**
     * Layouts the views.
     */
    private fun layout(recycler: Recycler, state: State) {
        val heightUsed =  height / 2
        var y = 0
        var x = 0
        detachAndScrapAttachedViews(recycler)

        // First row
        for (position in 0 until getFirstRowItemCount()) {

            val rootView = recycler.getViewForPosition(position)
            addView(rootView)

            val layoutParams = rootView.layoutParams
            layoutParams.height = heightUsed
            rootView.layoutParams = layoutParams

            measureChildWithMargins(rootView, 0, 0)
            layoutDecoratedWithMargins(
                rootView,
                x + paddingLeft,
                0,
                paddingLeft + x + rootView.measuredWidth,
                rootView.measuredHeight
            )
            x += rootView.measuredWidth
            y = maxOf(heightUsed, rootView.measuredHeight)
        }

        // Second row
        x = 0
        for (position in getFirstRowItemCount() until state.itemCount) {

            val rootView = recycler.getViewForPosition(position)
            addView(rootView)

            val layoutParams = rootView.layoutParams
            layoutParams.height = heightUsed
            rootView.layoutParams = layoutParams

            measureChildWithMargins(rootView, 0, 0)
            layoutDecoratedWithMargins(
                rootView,
                x,
                y,
                paddingLeft + x + rootView.measuredWidth,
                y + rootView.measuredHeight
            )
            x += rootView.measuredWidth
        }

        recycler.scrapList.forEach {
            recycler.recycleView(it.itemView)
        }

    }

}
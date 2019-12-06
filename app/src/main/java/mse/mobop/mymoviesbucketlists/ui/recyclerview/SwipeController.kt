package mse.mobop.mymoviesbucketlists.ui.recyclerview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import mse.mobop.mymoviesbucketlists.R

abstract class SwipeController(swipeDir: Int): ItemTouchHelper.SimpleCallback(0, swipeDir) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        c.clipRect(0f, viewHolder.itemView.top.toFloat(),
            dX, viewHolder.itemView.bottom.toFloat())
        val width = viewHolder.itemView.width
        if(dX < width / 3)
            c.drawColor(Color.GRAY)
        else
            c.drawColor(Color.RED)

        val trashBinIcon = recyclerView.context.resources.getDrawable(R.drawable.ic_delete, null)
        val height = viewHolder.itemView.height
        val margin = 10
        val scale = 2
        trashBinIcon.bounds = Rect(
            margin,
            viewHolder.itemView.top + (height - trashBinIcon.intrinsicHeight * scale) / 2,
            margin + trashBinIcon.intrinsicWidth * scale,
            viewHolder.itemView.bottom - (height - trashBinIcon.intrinsicHeight * scale) / 2
        )
        trashBinIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
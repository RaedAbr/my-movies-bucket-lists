package mse.mobop.mymoviesbucketlists.ui.recyclerview

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import mse.mobop.mymoviesbucketlists.R


class SelectSwipeController(swipeDir: Int): ItemTouchHelper.SimpleCallback(0, swipeDir) {
    private var swipeReleased = false
    private var onSwipeReleasedAction: OnSwipeReleasedAction? = null
    fun setOnSwipeReleasedAction(onSwipeReleasedAction: OnSwipeReleasedAction) {
        this.onSwipeReleasedAction = onSwipeReleasedAction
    }

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
        c.clipRect(dX, viewHolder.itemView.top.toFloat(), viewHolder.itemView.right.toFloat(), viewHolder.itemView.bottom.toFloat())
        val width = viewHolder.itemView.width
        if(-dX < width / 3)
            c.drawColor(Color.GRAY)
        else
            c.drawColor(ContextCompat.getColor(recyclerView.context, R.color.colorSelected))

        val trashBinIcon = recyclerView.context.resources.getDrawable(R.drawable.ic_check, null)
        val height = viewHolder.itemView.height
        val margin = 10
        val scale = 2
        trashBinIcon.bounds = Rect(
            viewHolder.itemView.right - margin - trashBinIcon.intrinsicWidth * scale,
            viewHolder.itemView.top + (height - trashBinIcon.intrinsicHeight * scale) / 2,
            viewHolder.itemView.right - margin,
            viewHolder.itemView.bottom - (height - trashBinIcon.intrinsicHeight * scale) / 2
        )
        trashBinIcon.draw(c)

        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeReleased = (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP)
            Log.e("swipeBack", "$swipeReleased")
            if (swipeReleased && onSwipeReleasedAction != null && viewHolder.adapterPosition > -1) {
                onSwipeReleasedAction!!.onSwipeReleased(viewHolder.adapterPosition)
            }
            false
        }
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeReleased) {
            swipeReleased = false
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    interface OnSwipeReleasedAction {
        fun onSwipeReleased(position: Int)
    }
}
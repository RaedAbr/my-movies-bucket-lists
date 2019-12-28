package mse.mobop.mymoviesbucketlists.ui.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import mse.mobop.mymoviesbucketlists.R
import kotlin.math.max
import kotlin.math.min


@SuppressLint("ClickableViewAccessibility")
class ItemSwipeController(
    private val buttonsActions: OnSwipedListener,
    private val directions: Int): ItemTouchHelper.Callback() {

    private var swipeBack = false
    private var buttonShowedState = ButtonsState.GONE
    private var buttonInstance: Rect? = null
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var buttonWidth: Int = 0

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(
            0, directions
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var dx = dX
        if (actionState == ACTION_STATE_SWIPE) {
            if (buttonShowedState !== ButtonsState.GONE) {
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                    dx = max(dX, buttonWidth.toFloat())
                }
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    dx = min(dX, -buttonWidth.toFloat())
                }
                super.onChildDraw(c, recyclerView, viewHolder, dx, dY, actionState, isCurrentlyActive)
            } else {
                setTouchListener(c, recyclerView, viewHolder, dx, dY, actionState, isCurrentlyActive)
            }
        }
        if (buttonShowedState === ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dx, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth) {
                    buttonShowedState = ButtonsState.RIGHT_VISIBLE
                } else if (dX > buttonWidth) {
                    buttonShowedState  = ButtonsState.LEFT_VISIBLE
                }

                if (buttonShowedState !== ButtonsState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dY, actionState, isCurrentlyActive)
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dY, actionState, isCurrentlyActive)
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive)
                recyclerView.setOnTouchListener { _, _ -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false
                if (buttonInstance != null && buttonInstance!!.contains(event.x.toInt(), event.y.toInt())) {
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                        buttonsActions.onEditButtonClick(viewHolder.adapterPosition)
                    } else if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions.onDeleteButtonClick(viewHolder.adapterPosition)
                    }
                }
                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }

    fun setItemsClickable(
        recyclerView: RecyclerView,
        isClickable: Boolean
    ) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun drawButtons(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val context = viewHolder.itemView.context
        val buttonWidthWithoutPadding = buttonWidth - 20
        val itemView: View = viewHolder.itemView
        val p = Paint()

        val rightButton = Rect(
            itemView.right - buttonWidthWithoutPadding,
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        p.color = context.getColor(R.color.colorPrimaryDark)
//        c.drawRoundRect(rightButton.toRectF(), corners, corners, p)
        c.drawCircle(
            rightButton.exactCenterX(),
            rightButton.exactCenterY(),
            rightButton.width().toFloat() / 2,
            p)

        val leftButton = Rect(
            itemView.left,
            itemView.top,
            itemView.left + buttonWidthWithoutPadding,
            itemView.bottom
        )
//        c.drawRoundRect(leftButton.toRectF(), corners, corners, p)
        c.drawCircle(
            leftButton.exactCenterX(),
            leftButton.exactCenterY(),
            leftButton.width().toFloat() / 2,
            p)

        drawIcon(c, leftButton, context.getDrawable(R.drawable.ic_edit)!!)
        drawIcon(c, rightButton, context.getDrawable(R.drawable.ic_delete)!!)

        buttonInstance = null
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton
        }
        if (buttonShowedState === ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton
        }
    }

    private fun drawIcon(
        c: Canvas,
        button: Rect,
        icon: Drawable
    ) {
        val scale = 1
        icon.bounds = Rect(
            button.left + button.width() / 2 - icon.intrinsicWidth * scale / 2,
            button.top + button.height() / 2 - icon.intrinsicHeight * scale / 2,
            button.left + button.width() / 2 + icon.intrinsicWidth * scale / 2,
            button.top + button.height() / 2 + icon.intrinsicHeight * scale / 2
        )
        icon.draw(c)
    }

    fun onDraw(c: Canvas) {
        if (currentItemViewHolder != null) {
            val context = currentItemViewHolder!!.itemView.context
            buttonWidth = context.resources.getDimension(R.dimen.float_button_diametre).toInt()
            drawButtons(c, currentItemViewHolder!!)
        }
    }

    interface OnSwipedListener {
        fun onDeleteButtonClick(position: Int)
        fun onEditButtonClick(position: Int)
    }

    internal enum class ButtonsState {
        GONE, LEFT_VISIBLE, RIGHT_VISIBLE
    }
}
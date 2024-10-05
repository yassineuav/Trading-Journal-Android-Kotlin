package com.yassine.journaltrading.ui.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.yassine.journaltrading.R
import com.yassine.journaltrading.data.TradeHistory

class SwipeToDeleteCallback (
    private val context: Context,
    private val adapter: TradeAdapter,
    private val onEdit: (TradeHistory) -> Unit,
    private val onDelete: (TradeHistory) -> Unit,

    ) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {


    private val editIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.edit)!!
    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.delete)!!
    private val editBackground = ColorDrawable(Color.GREEN)
    private val deleteBackground = ColorDrawable(Color.RED)
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        isAntiAlias = true
    }


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val item = adapter.getTradeAtPosition(position)

        if (direction == ItemTouchHelper.RIGHT) {
            // Edit
            onEdit(item)
            adapter.notifyItemChanged(position) // Reset swipe animation
        } else if (direction == ItemTouchHelper.LEFT) {
            // Delete
//            onDelete(item)
            AlertDialog.Builder(context).apply {
                setTitle("Delete Trade")
                setMessage("Are you sure you want to delete this trade with margin ?")
                setPositiveButton("Yes") { _, _ ->
                    onDelete(item)
                    adapter.notifyItemRemoved(position)
                }
                setNegativeButton("No") { dialog, _ ->
                    adapter.notifyItemChanged(position)  // Restore item if deletion is cancelled
                    dialog.dismiss()
                }
                create()
                show()
            }
        }
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
        val itemView = viewHolder.itemView
        val icon: Drawable
        val background: ColorDrawable
        val text: String

        // Determine the background and icon based on the swipe direction
        when {
            dX > 0 -> { // Swiping to the right (Edit)
                background = editBackground
                icon = editIcon
                text = "Edit"
            }
            dX < 0 -> { // Swiping to the left (Delete)
                background = deleteBackground
                icon = deleteIcon
                text = "Delete"
            }
            else -> {
                // No swipe, do nothing
                background = ColorDrawable(Color.TRANSPARENT)
                icon = ColorDrawable(Color.TRANSPARENT) // Set to transparent
                text = ""
            }
        }

        // Draw the background
        background.setBounds(
            if (dX > 0) itemView.left else itemView.right + dX.toInt(),
            itemView.top,
            if (dX > 0) itemView.left + dX.toInt() else itemView.right,
            itemView.bottom
        )
        background.draw(c)

        // Calculate position for the icon
        val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (dX > 0) { // Right swipe (Edit)

            val iconLeft = itemView.left + iconMargin
            val iconRight = iconLeft + icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

            // Set larger text size for "Edit"
            textPaint.textSize = 50f  // Adjust text size as needed

            // Adjust the position of the text with padding and margin
            val textMargin = 20f  // Adjust this value for more or less space
            val textX = iconRight + textMargin
            val textY = itemView.top + itemView.height / 2f + textPaint.textSize / 2f

            // Draw the background, icon, and text
            editBackground.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            editBackground.draw(c)

            icon.draw(c)
            c.drawText("Edit", textX, textY, textPaint)


        } else if (dX < 0) { // Left swipe (Delete)

            val iconRight = itemView.right - iconMargin
            val iconLeft = iconRight - icon.intrinsicWidth
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            // Adjust the position of the text with padding and margin
            val textMargin = 120f  // Adjust this value for more or less space
            val textX = iconLeft - iconMargin.toFloat() - textMargin
            val textY = iconTop + icon.intrinsicHeight / 2f + textPaint.textSize / 2f

            textPaint.textSize = 40f

            icon.draw(c)
            c.drawText(text, textX, textY, textPaint)

        }

        // Draw the icon
        icon.draw(c)

        // Call super to continue drawing other UI elements (like swipe animation)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
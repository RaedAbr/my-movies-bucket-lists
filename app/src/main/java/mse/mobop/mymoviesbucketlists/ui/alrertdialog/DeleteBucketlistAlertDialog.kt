package mse.mobop.mymoviesbucketlists.ui.alrertdialog

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import mse.mobop.mymoviesbucketlists.R

class DeleteBucketlistAlertDialog(
    context: Context,
    private val positiveButtonAction: DialogInterface.OnClickListener? = null) {

    private val alertDialog = AlertDialog.Builder(context)

    init {
        alertDialog
            .apply {
                setPositiveButton(R.string.delete, positiveButtonAction)
                setNegativeButton(R.string.cancel) { _, _ -> }
                setTitle(R.string.remove_bucketlist_dialog_title)
                setMessage(R.string.remove_bucketlist_dialog_message)
            }
    }

    fun create(): AlertDialog.Builder {
        alertDialog.create()
        return alertDialog
    }

    fun show() {
        alertDialog.show()
    }
}
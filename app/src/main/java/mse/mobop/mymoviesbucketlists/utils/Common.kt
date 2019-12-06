package mse.mobop.mymoviesbucketlists.utils

import android.app.Activity
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.Timestamp
import java.util.*

fun hideKeyboardFrom(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun dateConverter(timestamp: Timestamp): CharSequence {
    val datetime = DateFormat.format("dd.MM.yyyy 'at' HH:mm:ss", timestamp.toDate())
    return datetime ?: ""
}
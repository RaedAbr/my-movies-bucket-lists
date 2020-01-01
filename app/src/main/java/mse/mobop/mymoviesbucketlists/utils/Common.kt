package mse.mobop.mymoviesbucketlists.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.Timestamp

fun hideKeyboardFrom(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun dateConverter(timestamp: Timestamp): CharSequence {
    val datetime = DateFormat.format("dd.MM.yyyy '-' HH:mm:ss", timestamp.toDate())
    return datetime ?: ""
}

fun getAttributeColor(context: Context, attributeId: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attributeId, typedValue, true)
    val colorRes = typedValue.resourceId
    val color: Int
    try {
        color = context.getColor(colorRes)
    } catch (e: NotFoundException) {
        throw NotFoundException("Not found color resource by id: $colorRes")
    }
    return color
}

fun getAttributeDrawable(context: Context, attributeId: Int): Drawable? {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attributeId, typedValue, true)
    val drawableRes = typedValue.resourceId
    val drawable: Drawable?
    try {
        drawable = context.getDrawable(drawableRes)
    } catch (e: NotFoundException) {
        throw NotFoundException("Not found drawable resource by id: $drawableRes")
    }
    return drawable
}
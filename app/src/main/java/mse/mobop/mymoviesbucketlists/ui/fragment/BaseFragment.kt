package mse.mobop.mymoviesbucketlists.ui.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    var fragmentTitle: String? = null
    set(value) {
        activity?.let {
            it as AppCompatActivity
            it.supportActionBar?.let {actionBar ->
                actionBar.title = value
            }
        }
        field = value
    }
}
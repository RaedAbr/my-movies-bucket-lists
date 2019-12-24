package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.firebase.firestore.Exclude
import kotlinx.android.synthetic.main.item_user.view.*

class SearchUserAdapter(context: Context,
                        private val viewRes: Int,
                        private val usersList: ArrayList<UserForSearch>):
    ArrayAdapter<SearchUserAdapter.UserForSearch>(context, viewRes, usersList){

    private var listener: OnCheckedChangeListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val layoutInflater = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate( viewRes, parent, false)
        }
        val user = usersList[position]

        view!!.username_textview.text = user.name

        view.is_user_selected.setOnCheckedChangeListener(null)
        view.is_user_selected.isChecked = user.isSelected

        view.is_user_selected.setOnCheckedChangeListener { _, isChecked ->
            listener!!.onCheckedChange(user, isChecked)
        }

        return view
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.listener = listener
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(user: UserForSearch, isChecked: Boolean)
    }

    data class UserForSearch(
        var id: String? = null,
        var name: String = "",
        @Exclude var isSelected: Boolean = false
    )

}
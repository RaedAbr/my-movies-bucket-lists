package mse.mobop.mymoviesbucketlists.ui.recyclerview.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.item_user.view.*
import mse.mobop.mymoviesbucketlists.model.User


open class SearchUserAdapter(
    context: Context,
    private val viewRes: Int,
    private val usersList: ArrayList<User>
): ArrayAdapter<User>(context, viewRes, usersList){

    private var listener: OnCheckedChangeListener? = null

    fun addAllUsers(list: ArrayList<User>) {
        usersList.addAll(list)
        notifyDataSetChanged()
    }

    fun addUser(user: User) {
        usersList.add(0, user)
        notifyDataSetChanged()
    }

    fun countWhere(function: (User) -> Boolean): Int {
        return usersList.count { function(it) }
    }

    fun items(): ArrayList<User> {
        return usersList
    }

    fun removeUser(user: User) {
        usersList.removeIf { it.id == user.id }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val layoutInflater = context.getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate( viewRes, parent, false)
        }
        val user = usersList[position]

        view!!.username_textview.text = user.name
        view.is_user_selected.isChecked = true

        view.is_user_selected.setOnCheckedChangeListener { _, _ ->
            listener?.onCheckedChange(user)
        }

        return view
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        this.listener = listener
    }

    interface OnCheckedChangeListener {
        fun onCheckedChange(user: User)
    }

}
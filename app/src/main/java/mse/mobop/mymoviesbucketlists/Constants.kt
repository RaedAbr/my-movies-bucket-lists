package mse.mobop.mymoviesbucketlists

const val ARG_BUCKETLIST_OBJECT = "mse.mobop.ui.fragment.bucketlist.Object"
const val ARG_ADD_EDIT_BUCKETLIST_FRAGMENT_ACTION = "mse.mobop.ui.fragment.bucketlist.FragmentAction"

enum class BucketlistAction {
    ADD, EDIT
}

val BucketListActionStrings = mapOf(
    BucketlistAction.ADD to "New list",
    BucketlistAction.EDIT to "Edit list"
)
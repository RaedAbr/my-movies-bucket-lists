package mse.mobop.data

data class OneList (
    var name: String,
    var createdBy: String,
    var creationDate: String,
    var creationTime: String
) {
    companion object {
        val myLists = ArrayList<OneList>()

        init {
            myLists.add(OneList("OneList 1", "Me", "November 18, 2019", "04:30PM"))
            myLists.add(OneList("OneList 2", "Bob", "September 20, 2019", "00:15AM"))
            myLists.add(OneList("OneList 3", "Alice", "June 2, 2019", "10:54AM"))
        }
    }
}
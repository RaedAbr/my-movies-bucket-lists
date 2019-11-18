package mse.mobop.data

enum class Category {
    PRIVATE, PROFESSIONAL
}

data class Todo (
    var title: String,
    var description: String?,
    var category: Category,
    var isDone: Boolean
) {
    companion object {
        val list = ArrayList<Todo>()

        init {
            list.add(Todo("Vacation", "Plan trip to Asia", Category.PRIVATE, false))
            list.add(Todo("MobOp", "Practical work", Category.PROFESSIONAL, false))
            list.add(Todo("Appointment", "For new job", Category.PROFESSIONAL, false))
        }
    }
}
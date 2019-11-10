package lesson7.ants

class Route(val list: MutableList<Choosable> = mutableListOf()) : MutableList<Choosable> by list {

    var limitation = 0.0
        private set
    var cost = 0.0
        private set

    override fun add(element: Choosable): Boolean {
        limitation += element.limitation
        cost += element.value
        return list.add(element)
    }

    fun toChoosableList(): List<Choosable> {
        return list
    }
}
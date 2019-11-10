package lesson7.ants

abstract class ChoosableSet(val set: MutableSet<Choosable> = mutableSetOf()) : Set<Choosable> by set {

    abstract fun tabooPaths(route: Route): Set<Choosable>
}
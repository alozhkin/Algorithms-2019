package lesson7.ants

abstract class ChoosableSet(val set: MutableSet<Choosable> = mutableSetOf()) : Set<Choosable> by set {

    abstract fun validPaths(route: Route, lastValidPaths: Set<Choosable>): Set<Choosable>
}
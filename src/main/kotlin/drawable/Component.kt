import coordinates.Coordinate
import coordinates.System

abstract class Component : GroupMember() {
    abstract var system: System
    abstract val interfaces: List<Interface>

    abstract fun clone(parentModel: Model): Component

    // Move this InterfaceComponent so that the given interface (asserted to
    // belong to this) has its center at targetItfPosition
    abstract fun move(itf: Interface, targetItfPosition: Coordinate)

    fun getConnectedTraces(model: Model): List<Trace> =
        model.getTracesRecursively().filter {
            it.segments.any {
                it.start.hostInterface in interfaces
                        || it.end.hostInterface in interfaces
            }
        }
}

import coordinates.Coordinate
import coordinates.System

interface Component {
    var system: System
    val bounds: Poly

    fun clone(parentModel: Model): Component
}

interface InterfaceComponent {
    // Move this InterfaceComponent so that the given interface (asserted to
    // belong to this) has its center at targetItfPosition
    fun move(itf: Interface, targetItfPosition: Coordinate)

    val interfaces: List<Interface>
}

import coordinates.System

interface Component {
    var system: System
    val bounds: Poly

    fun clone(parentModel: Model): Component
}

interface InterfaceComponent {
    val interfaces: List<Interface>
}

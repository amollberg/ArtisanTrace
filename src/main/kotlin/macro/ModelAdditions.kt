import coordinates.Coordinate
import coordinates.System
import java.io.File

data class ModelAdditions(val model: Model) {
    val system: System get() = model.system

    private var interfaces: MutableList<Interface> = mutableListOf()
    private var traces: MutableList<Trace> = mutableListOf()
    private var svgComponents: MutableList<SvgComponent> = mutableListOf()

    fun addInterface(itf: Interface) {
        interfaces.add(itf)
    }

    fun addTrace(trace: Trace) {
        traces.add(trace)
    }

    fun addSvg(backingFile: File, coordinate: Coordinate): SvgComponent {
        val svgComponent = model.loadSvg(backingFile, coordinate)
        svgComponents.add(svgComponent)
        return svgComponent
    }

    fun commit() {
        model.interfaces.addAll(interfaces)
        model.traces.addAll(traces)
        svgComponents.forEach { model.addSvg(it) }

        interfaces.clear()
        traces.clear()
        svgComponents.clear()
    }

    fun draw(drawer: OrientedDrawer) {
        isolatedStyle(drawer.drawer, stroke = model.color) {
            traces.forEach { it.draw(drawer) }
            interfaces.forEach { it.draw(drawer) }
            svgComponents.forEach { it.draw(drawer) }
        }
    }
}

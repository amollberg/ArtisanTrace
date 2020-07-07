import Svg.Companion.fromFile
import coordinates.Coordinate
import coordinates.System
import coordinates.System.Companion.root
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.openrndr.color.ColorRGBa
import org.openrndr.shape.CompositionDrawer
import org.openrndr.svg.writeSVG
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))

@Serializable
class Model(@Transient val system: System = root()) : FileBacked {
    var interfaces: MutableList<Interface> = mutableListOf()
    var traces: MutableList<Trace> = mutableListOf()
    var sketchComponents: MutableList<SketchComponent> = mutableListOf()
    var svgComponents: MutableList<SvgComponent> = mutableListOf()
    var groups: MutableList<Group> = mutableListOf()

    @Transient
    var polys: MutableList<Poly> = mutableListOf()

    // Interfaces from SVG components. Cannot be mixed into the other
    // interfaces because these interfaces shall not be group members by
    // themselves, but rather through their respective SVG component
    val svgInterfaces: List<Interface>
        get() = svgComponents.flatMap { it.interfaces }

    @Serializable(with = ColorRGBaSerializer::class)
    var color = ColorRGBa.PINK

    @Transient
    override var backingFile = File("default.ats")

    companion object {
        fun loadFromFile(file: File): Model? {
            if (!file.isFile) {
                println("$file does not exist")
                return null
            }
            var model = deserialize(file.readText(), file) ?: return null
            return model
        }

        internal fun deserialize(string: String, backingFile: File): Model? {
            val model =
                if (string.length > 0) json.parse(serializer(), string)
                else Model()
            model.backingFile = backingFile
            return postProcessDeserialized(model)
        }

        private fun postProcessDeserialized(model: Model): Model {
            // Note: Sketch components must be loaded before traces in order
            // to have all interfaces in components loaded before being
            // dereferenced by the trace terminals
            model.sketchComponents.forEach {
                it.model = replaceComponentModel(it.model, model)
                // Connect the model system with the component system
                it.model.setReference(it.system)
                // Connect the component system to the top-level model root
                replaceComponentReferenceSystem(it, model)

                it.model.relativizeBackingFileTo(model.workingDir)
            }
            // Re-index interfaces to the combined model
            model.getInterfacesRecursively().forEachIndexed { i, itf ->
                itf.id = i
            }
            model.traces.forEach { trace ->
                trace.segments.forEach {
                    it.start = replaceInterfaceUsingModel(it.start, model)
                    it.end = replaceInterfaceUsingModel(it.end, model)
                }
                trace.setCoordinateSystem(model.system)
            }
            (model.interfaces + model.svgInterfaces).forEach {
                it.center = model.system.coord(it.center.xy())
            }
            model.svgComponents.forEach { svgComponent ->
                svgComponent.svg = loadFromBackingFile(svgComponent.svg, model)
                svgComponent.svg.relativizeBackingFileTo(model.workingDir)
                replaceComponentReferenceSystem(svgComponent, model)
                svgComponent.interfaces = svgComponent.interfaces.map { itf ->
                    model.getInterfacesRecursively()
                        .first { it.id == itf.id }
                }.toMutableList()
                svgComponent.interfaces.forEach {
                    // We have forcibly set all coordinates to their
                    // intrinsic xy in the model system above, so here we know
                    // that the true position is the current xy but in the svg
                    // component system.
                    it.center = svgComponent.system.coord(it.center.xy())
                }
            }
            reconstructGroups(model)
            return model
        }

        /** Use the ID to replace the interface with the correct instance
         *  from the view model interface list.
         */
        private fun replaceInterfaceUsingModel(
            terminals: Terminals,
            model: Model
        ) = Terminals(
            model.getInterfacesRecursively()
                .first { it.id == terminals.hostInterface.id },
            terminals.range
        )

        /** Use the backingFile to load the model with the correct content */
        private fun replaceComponentModel(
            componentModel: Model,
            model: Model
        ): Model {
            val path =
                model.workingDir.resolve(componentModel.backingFile.toPath())
                    .toFile()
            return loadFromFile(path) ?: throw SerializationException(
                "Sketch component from file '$path' could not be loaded"
            )
        }

        private fun loadFromBackingFile(componentSvg: Svg, model: Model): Svg {
            val path =
                model.workingDir.resolve(componentSvg.backingFile.toPath())
                    .toFile()
            return fromFile(path.absoluteFile)
        }

        private fun replaceComponentReferenceSystem(
            component: Component,
            model: Model
        ) {
            component.system.reference?.let { assertIsRootSystem(it) }
            component.system.reference = model.system
        }

        private fun reconstructGroups(model: Model) {
            val maxGroupId = model.groupMembers.map {
                it.groupId
            }.max() ?: -1
            if (maxGroupId > -1) {
                model.groups = (0..maxGroupId).map { groupId ->
                    fun <T : GroupMember> correctMembers(groupMembers: List<T>) =
                        groupMembers.filter { it.groupId == groupId }
                            .toMutableSet()
                    Group(
                        correctMembers(model.interfaces),
                        correctMembers(model.traces),
                        correctMembers(model.sketchComponents),
                        correctMembers(model.svgComponents)
                    )
                }.toMutableList()
            }
        }
    }

    val components: List<Component> get() = sketchComponents + svgComponents

    // Note: The initial empty list is needed to help the compiler to infer
    // the correct type of the list for the plus operations
    // Note: Intentionally does not include svgInterfaces because they shall
    // not be group members by themselves.
    val groupMembers: List<GroupMember>
        get() = listOf<GroupMember>() +
                sketchComponents +
                svgComponents +
                interfaces +
                traces

    val workingDir: Path
        get() = (backingFile.toPath().toAbsolutePath().parent.toFile()
            ?: Paths.get("").toFile().absoluteFile).toPath()

    fun saveToFile() {
        backingFile.writeText(serialize())
    }

    fun exportToSvg() {
        val cd = CompositionDrawer()
        setStyle(cd, ViewModel.DEFAULT_STYLE)
        draw(
            OrientedDrawer(cd, system, false),
            interfacesToIgnore = getInterfacesRecursively().toSet()
        )
        svgFile.writeText(writeSVG(cd.composition))
    }

    private val svgFile: File get() = File(backingFile.path + ".svg")

    internal fun serialize(): String {
        getInterfacesRecursively().forEachIndexed { i, itf -> itf.id = i }
        groups.forEachIndexed { groupId, group ->
            group.members.forEach { it.groupId = groupId }
        }
        return json.stringify(serializer(), this)
    }

    fun draw(
        drawer: OrientedDrawer,
        interfacesToIgnore: Set<Interface>
    ) {
        isolatedStyle(drawer.drawer, stroke = color) {
            groups.forEach { it.draw(drawer) }
            polys.forEach { it.draw(drawer) }
            traces.forEach { it.draw(drawer) }
            (interfaces - interfacesToIgnore).forEach { it.draw(drawer) }
            svgComponents.forEach { it.draw(drawer) }
            sketchComponents.forEach {
                it.draw(drawer, interfacesToIgnore)
            }
        }
    }

    /** Return all interfaces that are connected to a trace */
    fun connectedInterfaces(): Set<Interface> =
        getTracesRecursively().flatMap {
            it.segments.map {
                setOf(
                    it.getStart().hostInterface,
                    it.getEnd().hostInterface
                )
            }
        }.fold(setOf()) { acc, itfSet -> acc.union(itfSet) }

    fun setReference(reference: System) {
        system.reference = reference
    }

    fun getInterfacesRecursively(): List<Interface> =
        interfaces + svgInterfaces + sketchComponents.flatMap {
            it.model.getInterfacesRecursively()
        }

    fun getTracesRecursively(): List<Trace> =
        traces + sketchComponents.flatMap {
            it.model.getTracesRecursively()
        }

    fun getSvgComponentsRecursively(): List<SvgComponent> =
        svgComponents + sketchComponents.flatMap {
            it.model.getSvgComponentsRecursively()
        }

    fun inferSvgInterfaces() {
        svgComponents.forEach {
            it.inferInterfaces()
        }
    }

    fun addSvg(backingFile: File, position: Coordinate): SvgComponent {
        val svg = fromFile(backingFile)
        val svgSystem = system.createSystem(origin = position.xyIn(system))
        val svgComponent = SvgComponent(svg, svgSystem)
        svgComponent.svg.relativizeBackingFileTo(workingDir)
        svgComponents.add(svgComponent)
        svgComponent.inferInterfaces()
        return svgComponent
    }

    fun addSketch(backingFile: File, position: Coordinate): SketchComponent? {
        var submodel = loadFromFile(backingFile)
        return submodel?.ifPresent {
            submodel.relativizeBackingFileTo(workingDir)
            val sketch = SketchComponent(
                it,
                system.createSystem(origin = position.xyIn(system))
            )
            sketchComponents.add(sketch)
            sketch
        }
    }

    fun eraseSegmentsTo(itf: Interface) {
        // Note: Copying to avoid concurrent modification problems
        copy(traces).forEach { trace ->
            trace.segments.forEach { segment ->
                if (segment.start.hostInterface == itf
                    || segment.end.hostInterface == itf
                ) {
                    eraseSegment(segment)
                }
            }
        }
    }

    fun eraseSegment(segmentToErase: TraceSegment) {
        // Note: Copying to avoid concurrent modification problems
        copy(traces).forEach { trace ->
            var newTraces = mutableListOf(Trace(trace.system))
            trace.segments.forEach { segment ->
                if (segment == segmentToErase) {
                    // Do not add it. Start on a new trace
                    newTraces.add(Trace(trace.system))
                } else {
                    // Add the current segment to the current trace
                    newTraces.last().segments.add(segment)
                }
            }
            if (newTraces.size > 1) {
                val newNonEmptyTraces = newTraces
                    .filter { traces -> traces.segments.size > 0 }
                traces.remove(trace)
                traces.addAll(newNonEmptyTraces)
            }
        }
    }
}

fun assertIsRootSystem(system: System) {
    if (system.reference != null) throw IllegalArgumentException(
        "System ${system} should have reference 'null'"
    )
}

private fun <T> copy(l: List<T>) = l.toMutableList()

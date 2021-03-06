import TestUtils.Companion.assertListListEquals
import TestUtils.Companion.at
import TestUtils.Companion.clickMouse
import TestUtils.Companion.createViewModel
import TestUtils.Companion.dropFiles
import TestUtils.Companion.sendKey
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.math.Vector2
import java.io.File
import kotlin.math.PI

class SvgComponentTest {
    companion object {
        val VIA_SVG_PATH = "src/test/resources/Via2.svg"
        val SVG_PATH = "src/test/resources/IC1.svg"
        val DROP_ORIGIN = Vector2(47.0, 11.0)
        val MOVED_ORIGIN = Vector2(200.0, 34.0)
    }

    @Test
    fun interfacesFollowMovedSvg() {
        var original = createModel()
        // Save and load
        val deserialized = deserialize(original.serialize())
        // Modify
        val modified = modifyModel(deserialized)

        // check that the interfaces are in the right place
        val expectedInterfaceEnds = EXPECTED_INTERFACES[SVG_PATH]!!.map {
            it.ends.map { coord -> coord + MOVED_ORIGIN }
        }
        assertListListEquals(
            expectedInterfaceEnds,
            modified.svgInterfaces.map { itf ->
                itf.getEnds().map {
                    it.xyIn(modified.system)
                }
            },
            delta = 1e-5
        )
    }

    @Test
    fun inferInterfacesDroppedIc1() {
        var view = createViewModel(Model(root()))

        val SVG_PATH = "src/test/resources/IC1.svg"

        dropFiles(view, DropEvent(Vector2(47.0, 11.0), listOf(File(SVG_PATH))))

        val svgSystem = view.model.svgComponents.first().system
        val interfaceEnds = view.model.svgInterfaces.map { itf ->
            itf.getEnds().map { it.xyIn(svgSystem) }
        }
        assertListListEquals(
            EXPECTED_INTERFACES[SVG_PATH]!!.map { it.ends },
            interfaceEnds,
            delta = 1e-5
        )
    }

    @Test
    fun inferInterfacesLoadedIc1Sketch() {
        var view = ViewModel(createModel())

        val SKETCH_PATH =
            "src/test/resources/IC1_without_inferred_interfaces.ats"

        // Load the sketch
        dropFiles(view, DropEvent(Vector2.ZERO, listOf(File(SKETCH_PATH))))

        // Infer interfaces of all SVG components
        sendKey(view, "f")

        val svgSystem = view.model.svgComponents.first().system
        val interfaceEnds = view.model.svgInterfaces.map { itf ->
            itf.getEnds().map { it.xyIn(svgSystem) }
        }
        assertListListEquals(
            EXPECTED_INTERFACES[SVG_PATH]!!.map { it.ends },
            interfaceEnds,
            delta = 1e-5
        )
    }

    @Test
    fun componentClonesTransformation() {
        val model = createModel()
        val original = model.svgComponents[0]
        // Rotate and scale it
        original.system.axes *= Matrix22.rotation(45 * PI / 180)
        original.system.axes *= 1.4

        val clone = original.clone(model)
        assertEquals(clone.system, original.system)
    }

    @Test
    fun ic1IncludesInterfacesInConvexHull() {
        val model = createModel()
        val svgComponent = model.svgComponents[0]
        // Get the surface via a group
        val group = Group()
        group.add(svgComponent)
        model.groups.add(group)

        assertEquals(
            EXPECTED_INTERFACES[SVG_PATH]!!.size,
            group.surface.interfaces.size
        )
    }

    @Test
    fun via2IncludesInterfacesInConvexHull() {
        val model = createModel()
        dropFiles(
            createViewModel(model),
            DropEvent(DROP_ORIGIN, listOf(File(VIA_SVG_PATH)))
        )
        val svgComponent = model.svgComponents.last()
        // Get the surface via a group
        val group = Group()
        group.add(svgComponent)
        model.groups.add(group)

        assertEquals(
            1,
            group.surface.interfaces.size
        )
    }

    private fun createModel(): Model {
        var view = createViewModel(Model(root()))

        // Import svg by simulating mouse drag-and-drop
        dropFiles(
            view,
            DropEvent(DROP_ORIGIN, listOf(File(SVG_PATH)))
        )

        return view.model
    }

    private fun modifyModel(original: Model): Model {
        var view = createViewModel(original)

        view.changeTool(ComponentMoveTool(view))
        // Pick up the SVG component
        clickMouse(
            view,
            at(view, DROP_ORIGIN + OFFSET_TO_BOUNDING_BOX[SVG_PATH]!!)
        )
        // Put it down somewhere else
        clickMouse(
            view,
            at(view, MOVED_ORIGIN + OFFSET_TO_BOUNDING_BOX[SVG_PATH]!!)
        )

        return view.model
    }
}

private fun deserialize(value: String): Model =
    Model.deserialize(value, File("dontcare"))!!

package drawable

import Matrix22
import Model
import SvgComponentTest.Companion.DROP_ORIGIN
import TestUtils.Companion.createViewModel
import TestUtils.Companion.dropFiles
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openrndr.DropEvent
import org.openrndr.KEY_LEFT_SHIFT
import java.io.File
import kotlin.math.PI

class SketchComponentTest {

    companion object {
        const val SKETCH_PATH = "src/test/resources/IC1.ats"
    }

    @Test
    fun componentClonesTransformation() {
        val model = createModel()
        val original = model.sketchComponents[0]
        // Rotate and scale it
        original.system.axes *= Matrix22.rotation(45 * PI / 180)
        original.system.axes *= 1.4

        val clone = original.clone(model)
        assertEquals(clone.system, original.system)
    }

    private fun createModel(): Model {
        var view = createViewModel(Model(root()))

        // Import sketch by simulating mouse drag-and-drop
        dropFiles(
            view,
            DropEvent(DROP_ORIGIN, listOf(File(SKETCH_PATH))),
            setOf(KEY_LEFT_SHIFT)
        )

        return view.model
    }
}

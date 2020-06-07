import coordinates.Coordinate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.openrndr.*
import org.openrndr.math.Vector2

open class WithImplicitView {
    protected var view = ViewModel(Model(coordinates.System.Companion.root()))

    protected fun at(x: Int = 0, y: Int = 0) =
        Coordinate(Vector2(x.toDouble(), y.toDouble()), view.root)

    protected fun at(x: Double = 0.0, y: Double = 0.0) =
        Coordinate(Vector2(x, y), view.root)

    protected fun clickMouse(position: Coordinate) {
        view.mousePoint = position.relativeTo(view.root)
        view.activeTool.mouseClicked(view.mousePoint)
    }
}

class TestUtils {
    companion object {
        fun assertEquals(a: Coordinate, b: Coordinate) {
            assertEquals(a.system, b.system)
            assertEquals(a.xy(), b.xy())
        }

        fun assertEquals(a: Vector2, b: Vector2) {
            assertEquals(a.x, b.x, "$a != $b (x-coord)\n")
            assertEquals(a.y, b.y, "$a != $b (y-coord)\n")
        }

        fun assertEquals(a: Matrix22, b: Matrix22) {
            assertEquals(a.columnMajor, b.columnMajor)
        }

        fun assertEquals(a: List<Vector2>, b: List<Vector2>) {
            a.zip(b).forEach { (a, b) ->
                assertEquals(a, b)
            }
        }

        fun assertEquals(a: Model, b: Model) =
            assertEquals(toList(a), toList(b))

        fun assertNotEquals(a: Model, b: Model) =
            assertNotEquals(toList(a), toList(b))

        fun createViewModel(model: Model = Model()): ViewModel {
            val viewModel = ViewModel(model)
            viewModel.muteSerializationExceptions = false
            return viewModel
        }

        fun at(viewModel: ViewModel, x: Double, y: Double) =
            viewModel.root.coord(Vector2(x, y))

        fun at(viewModel: ViewModel, xy: Vector2) =
            viewModel.root.coord(xy)

        fun scrollMouse(
            view: ViewModel,
            count: Int,
            modifiers: Set<KeyModifier> = setOf()
        ) {
            view.activeTool.mouseScrolled(
                MouseEvent(
                    Vector2.ZERO,
                    Vector2(0.0, count.toDouble()),
                    Vector2.ZERO,
                    MouseEventType.SCROLLED,
                    MouseButton.NONE,
                    modifiers,
                    false
                )
            )
        }

        fun clickMouse(
            view: ViewModel,
            position: Coordinate
        ) {
            view.mousePoint = position.relativeTo(view.root)
            view.activeTool.mouseClicked(view.mousePoint)
        }

        fun dropFiles(
            view: ViewModel,
            dropEvent: DropEvent,
            modifiers: Set<Int> = setOf()
        ) {
            modifiers.forEach {
                view.modifierKeysHeld[it] = true
            }
            view.fileDrop(dropEvent)
            modifiers.forEach {
                view.modifierKeysHeld[it] = false
            }
        }

        private fun toList(model: Model) =
            listOf(model.interfaces, model.traces)
    }
}

import TestUtils.Companion.sendKey
import TestUtils.Companion.withModifiers
import coordinates.Coordinate
import coordinates.System.Companion.root
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.openrndr.*
import org.openrndr.math.Vector2
import java.io.File

open class WithImplicitView {
    protected var view = ViewModel(Model(root())).let {
        it.muteSerializationExceptions = false
        it
    }

    protected fun at(x: Int = 0, y: Int = 0) =
        Coordinate(Vector2(x.toDouble(), y.toDouble()), view.root)

    protected fun at(x: Double = 0.0, y: Double = 0.0) =
        Coordinate(Vector2(x, y), view.root)

    fun at(xy: Vector2) = Coordinate(xy, view.root)

    protected fun clickMouse(
        position: Coordinate,
        modifiers: Set<KeyModifier> = emptySet()
    ) {
        moveMouse(position)
        withModifiers(view, modifiers) {
            view.activeTool.mouseClicked(view.mousePoint)
        }
    }

    fun dropFiles(dropEvent: DropEvent, modifiers: Set<KeyModifier> = setOf()) {
        TestUtils.dropFiles(view, dropEvent, modifiers)
    }

    fun sendKey(name: String, modifiers: Set<KeyModifier> = setOf()) {
        sendKey(view, name, modifiers)
    }

    fun moveMouse(position: Coordinate) {
        view.mousePoint = position.relativeTo(view.root)
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

        fun assertEquals(a: Vector2, b: Vector2, delta: Double) {
            assertEquals(a.x, b.x, delta, "$a != $b (x-coord)\n")
            assertEquals(a.y, b.y, delta, "$a != $b (y-coord)\n")
        }

        fun assertEquals(a: Matrix22, b: Matrix22) {
            assertEquals(a.columnMajor, b.columnMajor)
        }

        fun assertListListEquals(
            a: List<List<Vector2>>, b: List<List<Vector2>>, delta: Double = 0.0
        ) {
            assertEquals(a.size, b.size)
            a.zip(b).forEach { (al, bl) ->
                assertListEquals(al, bl, delta)
            }
        }

        fun assertListEquals(
            al: List<Vector2>, bl: List<Vector2>, delta: Double = 0.0
        ) {
            al.zip(bl).forEach { (av, bv) ->
                assertEquals(av, bv, delta)
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

        fun at(viewModel: ViewModel, x: Int, y: Int) =
            viewModel.root.coord(Vector2(x.toDouble(), y.toDouble()))

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
            position: Coordinate,
            modifiers: Set<KeyModifier> = emptySet()
        ) {
            withModifiers(view, modifiers) {
                view.mousePoint = position.relativeTo(view.root)
                view.activeTool.mouseClicked(view.mousePoint)
            }
        }

        fun dropFiles(
            view: ViewModel,
            dropEvent: DropEvent,
            modifiers: Set<KeyModifier> = setOf()
        ) {
            withModifiers(view, modifiers) {
                view.fileDrop(dropEvent)
            }
        }

        fun withModifiers(
            view: ViewModel, modifiers: Set<KeyModifier>, fn: () -> Unit
        ) {
            val oldModifiers = view.modifierKeysHeld.toSet()
            view.modifierKeysHeld += modifiers
            fn()
            view.modifierKeysHeld = oldModifiers
        }

        fun sendKey(
            view: ViewModel,
            name: String,
            modifiers: Set<KeyModifier> = setOf()
        ) {
            view.keyDown(KeyEvent(KeyEventType.KEY_DOWN, 0, name, modifiers))
            view.keyUp(KeyEvent(KeyEventType.KEY_UP, 0, name, modifiers))
        }

        fun tempAtgFile(content: String): File {
            val tmpFile =
                File.createTempFile("tempAtgFile", ".atg", File("build"))
            tmpFile.deleteOnExit()
            tmpFile.writeText(content)
            return tmpFile
        }

        private fun toList(model: Model) =
            listOf(
                model.interfaces,
                model.traces,
                model.sketchComponents.map { toList(it) },
                model.svgComponents.map { toList(it) },
                model.groups.map { toList(it) }
            )

        private fun toList(sketchComponent: SketchComponent) =
            listOf(
                sketchComponent.groupId,
                sketchComponent.groupOrdinal,
                sketchComponent.system,
                sketchComponent.model.backingFile
            )

        private fun toList(svgComponent: SvgComponent) =
            listOf(
                svgComponent.groupId,
                svgComponent.groupOrdinal,
                svgComponent.system,
                svgComponent.svg.backingFile
            )

        private fun toList(group: Group) =
            listOf(
                group.id,
                group.members.sortedBy { it.groupOrdinal }
            )
    }
}

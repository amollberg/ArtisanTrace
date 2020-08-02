import Poly.Companion.rect
import TestUtils.Companion.assertEquals
import TestUtils.Companion.at
import org.junit.jupiter.api.Test
import org.openrndr.KeyModifier.CTRL
import org.openrndr.math.Vector2

class InterfaceMoveToolTest : WithImplicitView() {

    @Test
    fun moveInterfaceWithMouse() {
        val itf = Interface(at(x = 17.0), 0.0, 10.0, 4)
        view.model.interfaces = mutableListOf(itf)

        val tool = InterfaceMoveTool(view)
        view.changeTool(tool)
        clickMouse(itf.center)
        val newCenter = at(view, 47, 11)
        moveMouse(newCenter)
        tool.update(itf)

        assertEquals(newCenter, itf.center)
    }

    @Test
    fun snapToBoundsWhenAltHovering() {
        val boundaryX = 53
        val area = rect(view.root, 100, 100)
            .moved(view.root.length(Vector2(boundaryX.toDouble(), 0.0)))
        view.model.polys.add(area)

        val startItf = Interface(at(x = 10), 0.0, 10.0, 4)
        view.model.interfaces = mutableListOf(startItf)

        val tool = InterfaceMoveTool(view)
        view.changeTool(tool)
        clickMouse(startItf.center)
        clickMouse(at(view, boundaryX + 13, 20), setOf(CTRL))

        val expectedCenterX = (boundaryX
                - startItf.length / 2
                - InterfaceSnapSubtool.MARGIN_DISTANCE)
        assertEquals(at(view, expectedCenterX, 20.0), startItf.center)
    }
}

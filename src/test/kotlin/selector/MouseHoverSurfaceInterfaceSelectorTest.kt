import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MouseHoverSurfaceInterfaceSelectorTest : WithImplicitView() {

    @Test
    fun getInterface() {
        var itfs = listOf(
            Interface(at(y = 0.0), 0.0, 10.0, 1),
            Interface(at(y = 20.0), 0.0, 10.0, 1),
            Interface(at(y = 40.0), 0.0, 10.0, 1)
        )
        view.model.interfaces = itfs.toMutableList()
        view.model.groups.add(Group())
        itfs.forEach { view.model.groups.first().add(it) }
        var selection = MouseHoverSurfaceInterfaceSelector(view)

        view.mousePoint = at(y = 19.0)
        assertEquals(itfs[0], selection.getInterface())

        view.mousePoint = at(y = 21.0)
        assertEquals(itfs[2], selection.getInterface())
    }
}

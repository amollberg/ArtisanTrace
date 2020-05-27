import org.junit.jupiter.api.Assertions.*
import org.openrndr.math.Vector2

fun assertEquals(a: Vector2, b: Vector2) {
    assertEquals(a.x, b.x, "$a != $b (x-coord)\n")
    assertEquals(a.y, b.y, "$a != $b (y-coord)\n")
}

fun assertEquals(a: List<Vector2>, b: List<Vector2>) {
    a.zip(b).forEach { (a, b) ->
        assertEquals(a, b)
    }
}

fun assertEquals(a: ViewModel, b: ViewModel) =
    assertEquals(toList(a), toList(b))


fun assertNotEquals(a: ViewModel, b: ViewModel) =
    assertNotEquals(toList(a), toList(b))

private fun toList(viewModel: ViewModel) =
    listOf(
        viewModel.interfaces, viewModel.mousePoint, viewModel.traces,
        viewModel.modifierKeysHeld, viewModel.areInterfacesVisible,
        viewModel.activeTool.javaClass
    )

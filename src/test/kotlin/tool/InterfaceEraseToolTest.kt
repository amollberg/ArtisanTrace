import TestUtils.Companion.at
import TestUtils.Companion.clickMouse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class InterfaceEraseToolTest : WithImplicitView() {

    @Test
    fun noConnectedTrace() {
        createTraces(listOf(10), listOf(20, 30))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 10, 10))

        checkInterfaces(20, 30)
        checkTraces(listOf(20, 30))
    }

    @Test
    fun oneConnectedTrace() {
        createTraces(listOf(10), listOf(20, 30, 40, 50, 60))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 40, 40))

        checkInterfaces(10, 20, 30, 50, 60)
        checkTraces(listOf(20, 30), listOf(50, 60))
    }

    @Test
    fun oneConnectedTraceMultiplePlaces() {
        // Connections to 40 in two places
        createTraces(listOf(10), listOf(20, 30, 40, 50, 60, 40, 70, 80))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 40, 40))

        checkInterfaces(10, 20, 30, 50, 60, 70, 80)
        checkTraces(listOf(20, 30), listOf(50, 60), listOf(70, 80))
    }

    @Test
    fun multipleConnectedTraces() {
        // Both connected to 50
        createTraces(listOf(10, 20, 50, 80, 90), listOf(30, 40, 50, 60, 70))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 50, 50))

        checkInterfaces(10, 20, 30, 40, 60, 70, 80, 90)
        checkTraces(
            listOf(10, 20), listOf(80, 90), listOf(30, 40), listOf(60, 70)
        )
    }

    @Test
    fun firstInterface() {
        createTraces(listOf(10, 20, 30, 40))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 10, 10))

        checkInterfaces(20, 30, 40)
        checkTraces(listOf(20, 30, 40))
    }

    @Test
    fun secondInterface() {
        createTraces(listOf(10, 20, 30, 40))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 20, 20))

        checkInterfaces(10, 30, 40)
        checkTraces(listOf(30, 40))
    }

    @Test
    fun nextToLastInterface() {
        createTraces(listOf(10, 20, 30, 40))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 30, 30))

        checkInterfaces(10, 20, 40)
        checkTraces(listOf(10, 20))
    }

    @Test
    fun lastInterface() {
        createTraces(listOf(10, 20, 30, 40))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 40, 40))

        checkInterfaces(10, 20, 30)
        checkTraces(listOf(10, 20, 30))
    }

    @Test
    fun oneSegmentTrace() {
        createTraces(listOf(10, 20))

        view.changeTool(InterfaceEraseTool(view))
        clickMouse(view, at(view, 10, 10))

        checkInterfaces(20)
        checkTraces()
    }

    @Test
    fun sanityCheckHelpers() {
        checkInterfaces()
        checkTraces()

        createTraces(listOf(10, 20, 30), listOf(20, 40))
        checkInterfaces(10, 20, 30, 40)
        checkTraces(listOf(10, 20, 30), listOf(20, 40))
    }

    private fun createTraces(vararg traces: List<Int>) {
        traces.forEach { trace ->
            view.changeTool(InterfaceDrawTool(view))
            trace.forEach { position ->
                if (!isAnInterfaceAt(position)) {
                    clickMouse(view, at(view, position, position))
                }
            }

            view.changeTool(TraceDrawTool(view))
            trace.forEach { position ->
                clickMouse(view, at(view, position, position))
            }

            view.changeTool(EmptyTool(view))
        }
    }

    private fun isAnInterfaceAt(position: Int) =
        view.model.interfaces.any {
            it.center.xyIn(view.root).x.toInt() ==
                    position
        }

    private fun checkInterfaces(vararg expectedInterfaces: Int) {
        assertContainsEqual(expectedInterfaces.toList(), view.model
            .interfaces.map { it.center.xy().x.toInt() })
    }

    private fun checkTraces(vararg expectedTraces: List<Int>) {
        val actualTraces =
            view.model.traces.map {
                it.segments.map { it.start }
                    .plus(it.segments.last().end)
                    .map { it.hostInterface.center.xy().x.toInt() }
            }
        assertContainsEqual(expectedTraces.toList(), actualTraces)
    }

    /** Assert that the iterables contain equal objects, not necessarily in
     *  the same order.
     */
    private fun <T> assertContainsEqual(
        expected: Iterable<T>,
        actual: Iterable<T>
    ) {
        var e = expected.toMutableList()
        var a = actual.toMutableList()
        val failures = mutableListOf<String>()
        e.forEach {
            if (a.contains(it)) {
                a.remove(it)
            } else {
                failures.add("Expected $it but is missing")
            }
        }
        a.forEach {
            failures.add("Did not expect $it")
        }
        if (failures.isNotEmpty()) {
            fail(
                "expected $expected != actual $actual. \n" + failures
                    .joinToString()
            )
        }
    }
}

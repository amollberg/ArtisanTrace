import coordinates.Coordinate

class InterfaceEraseTool(viewModel: ViewModel) : BaseInterfaceTool(viewModel) {
    private val interfaceSelector = MouseHoverInterfaceSelector(
        viewModel,
        // Not possible to erase interfaces from components
        includeComponents = false
    )

    override fun mouseClicked(position: Coordinate) {
        val itf = interfaceSelector.getInterface() ?: return

        // Note: Copying to avoid concurrent modification problems
        copy(viewModel.model.traces).forEach {
            splitTraceAt(it, itf)
        }
        viewModel.model.interfaces.remove(itf)
    }

    private fun splitTraceAt(trace: Trace, itf: Interface) {
        var newTraces = mutableListOf(Trace(trace.system))
        trace.segments.forEach { segment ->
            if (segment.start.hostInterface == itf
                || segment.end.hostInterface == itf
            ) {
                // Start on a new trace
                newTraces.add(Trace(trace.system))
            } else {
                // Add the current terminals to the current trace
                newTraces.last().segments.add(segment)
            }
        }
        if (newTraces.size > 1) {
            val newNonEmptyTraces = newTraces
                .filter { traces -> traces.segments.size > 0 }
            viewModel.model.traces.remove(trace)
            viewModel.model.traces.addAll(newNonEmptyTraces)
        }
    }

    override fun draw(drawer: OrientedDrawer) {
        interfaceSelector.draw(drawer)
    }

    private fun <T> copy(l: List<T>) = l.toMutableList()
}

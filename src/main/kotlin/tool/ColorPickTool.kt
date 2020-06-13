package tool

import BaseTool
import ViewModel
import coordinates.Coordinate
import ifPresent
import selector.MouseHoverSvgSelector

class ColorPickTool(viewModel: ViewModel) : BaseTool(viewModel) {
    private val svgSelector = MouseHoverSvgSelector(viewModel)

    override fun mouseClicked(position: Coordinate) {
        val selectedSvgComponent = svgSelector.getSvgComponent() ?: return
        selectedSvgComponent.svg.composition?.findShapes()?.first()
            ?.effectiveStroke?.ifPresent { viewModel.model.color = it }
    }
}

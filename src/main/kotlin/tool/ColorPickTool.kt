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
        val xy = position.xyIn(selectedSvgComponent.system)
        selectedSvgComponent.svg.composition?.findShapes()
            ?.firstOrNull { it.shape.contains(xy) }?.effectiveStroke
            ?.ifPresent {
                viewModel.model.color = it
            }
    }
}

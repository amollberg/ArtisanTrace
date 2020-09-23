import coordinates.Coordinate
import org.openrndr.color.ColorRGBa.Companion.TRANSPARENT
import org.openrndr.color.ColorRGBa.Companion.YELLOW
import java.io.File

class ComponentSaveTool(viewModel: ViewModel) : BaseTool(viewModel) {
    var selectedComponents: Set<Component> = emptySet()
    internal val componentSelector = MouseHoverComponentSelector(viewModel)

    override fun mouseClicked(position: Coordinate) {
        // Select the component under the mouse
        val selectedComponent = componentSelector.getComponent() ?: return
        val fileBacked = when (selectedComponent) {
            is SvgComponent -> selectedComponent.svg
            is SketchComponent -> selectedComponent.model
            else -> throw
            IllegalArgumentException("Unknown Component type: $selectedComponent")
        }
        copyBackingFileToCwd(fileBacked)
        selectedComponents += selectedComponent
    }

    override fun draw(drawer: OrientedDrawer) {
        componentSelector.draw(drawer)
        isolatedStyle(drawer.drawer, stroke = YELLOW, fill = TRANSPARENT) {
            selectedComponents.forEach { component ->
                component.bounds.draw(drawer)
            }
        }
    }

    private fun copyBackingFileToCwd(fileBacked: FileBacked) {
        val sourceFile = fileBacked.backingFile
        // Copy backing file to current directory
        val targetFile = File(sourceFile.name)
        sourceFile.copyTo(targetFile, true)
    }
}

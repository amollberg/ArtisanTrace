import org.openrndr.DropEvent
import org.openrndr.KeyEvent

class MacroViewModel(model: Model) : ViewModel(model) {
    override fun fileDrop(drop: DropEvent) {
        val previouslyAdded: MutableList<Component> = mutableListOf()
        fun moveAddedComponent(component: Component) {
            moveToAvoidCollision(component, previouslyAdded)
            previouslyAdded += component
        }
        drop.files.forEach { droppedFile ->
            when (droppedFile.extension) {
                "svg" ->
                    handleDroppedSvgFile(
                        droppedFile,
                        root.coord(drop.position),
                        ::moveAddedComponent
                    )
                "atg" ->
                    (0..20).forEach { i ->
                        handleDroppedMacroFile(
                            droppedFile,
                            root.coord(drop.position),
                            ::moveAddedComponent
                        )
                    }
            }
        }
    }

    override fun keyUp(key: KeyEvent) {
        updateModifiers(key)
        when (key.name) {
            "q" -> {
                changeTool(EmptyTool(this))
            }
            "x" -> {
                toggleExtendedVisualization()
            }
            "c" -> {
                clearModel()
            }
            "s" -> {
                model.saveToFile()
                model.exportToSvg()
            }
            "y" -> {
                changeTool(ComponentMoveTool(this))
            }
            "f" -> {
                model.inferSvgInterfaces()
            }
            "u" -> {
                changeTool(ComponentEraseTool(this))
            }
            else -> {
                activeTool.keyUp(key)
            }
        }
    }

    private fun clearModel() {
        model = Model(root)
    }
}

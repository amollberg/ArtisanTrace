import coordinates.System.Companion.root
import org.openrndr.MouseButton
import org.openrndr.application

fun main() = application {
    configure {
        width = 768
        height = 576
        windowResizable = true
    }

    program {
        window.title = "ArtisanTrace"
        var viewModel = ViewModel(modelFromFileOrDefault(Model(root())))

        viewModel.modelLoaded.listen { loadedFile ->
            window.title = "${loadedFile.name} - ArtisanTrace"
        }
        window.drop.listen {
            viewModel.fileDrop(it)
        }
        mouse.moved.listen {
            viewModel.mousePoint = viewModel.root.coord(it.position)
        }
        mouse.clicked.listen {
            val position = viewModel.root.coord(it.position)
            when (it.button) {
                MouseButton.RIGHT ->
                    viewModel.activeTool.mouseRightClicked(position)
                else -> viewModel.activeTool.mouseClicked(position)
            }
        }
        mouse.scrolled.listen {
            viewModel.activeTool.mouseScrolled(it)
        }
        keyboard.keyUp.listen {
            viewModel.keyUp(it)
        }
        keyboard.keyDown.listen {
            viewModel.keyDown(it)
        }
        extend {
            setStyle(drawer, ViewModel.DEFAULT_STYLE)
            viewModel.draw(drawer)
        }
    }
}

fun modelFromFileOrDefault(defaultModel: Model) =
    Model.loadFromFile(Model(root()).backingFile) ?: defaultModel

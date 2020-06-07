import java.io.File

interface FileBacked {
    var backingFile: File

    /** Make the backingFile relative to (the backing file of) the model */
    fun relativizeBackingFileTo(model: Model) {
        backingFile = model.workingDir
            .relativize(backingFile.absoluteFile.toPath())
            .toFile()
    }
}

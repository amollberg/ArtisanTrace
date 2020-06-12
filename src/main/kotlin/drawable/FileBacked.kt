import java.io.File
import java.nio.file.Path

interface FileBacked {
    var backingFile: File

    /** Make the backingFile relative to the workingDir (of a model,
     * presumably)
     */
    fun relativizeBackingFileTo(workingDir: Path) {
        backingFile = workingDir
            .relativize(backingFile.absoluteFile.toPath())
            .toFile()
    }
}

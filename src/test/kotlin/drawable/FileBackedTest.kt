import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class FileBackedTest {
    @Test
    fun relativizeBackingFile() {
        assertEquals("IC1.ats", relativePath("IC1.ats", ""))
        assertEquals(
            "IC1.ats",
            relativePath("src/test/resources/IC1.ats", "src/test/resources")
        )
    }

    private fun relativePath(
        fileBackedPath: String,
        workingDir: String
    ): String {
        var fb = fileBacked(fileBackedPath)
        fb.relativizeBackingFileTo(File(workingDir).toPath().toAbsolutePath())
        return fb.backingFile.path
    }

    private fun fileBacked(backingFilePath: String): FileBacked {
        class FB(override var backingFile: File) : FileBacked {}
        return FB(File(backingFilePath))
    }
}

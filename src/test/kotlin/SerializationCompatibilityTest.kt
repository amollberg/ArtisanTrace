import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class SerializationCompatibilityTest {
    @Test
    fun loadTestSketch() {
        Model.loadFromFile(File("src/test/resources/IC1.ats"))
    }

    @Test
    fun loadSavedTestSketch() {
        val oldFile = File("src/test/resources/IC1.ats")
        val newFile = File("src/test/resources/IC1.ats.new")
        val model = Model.loadFromFile(oldFile)!!

        newFile.writeText(model.serialize())
        Model.loadFromFile(newFile)!!
    }

    @Test
    fun compareExistingWithNewlySerialized() {
        val oldFile = File("src/test/resources/IC1.ats")
        val newFile = File("src/test/resources/IC1.ats.new")
        val model = Model.loadFromFile(oldFile)!!

        newFile.writeText(model.serialize())
        assertEquals(oldFile.readText(), model.serialize())
    }
}

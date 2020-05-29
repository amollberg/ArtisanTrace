import org.junit.jupiter.api.Test

class Matrix22Test {
    @Test
    fun invertMatrix22() {
        var a = Matrix22(
            1.0, 3.14,
            -5.3, 0.0
        )
        assertEquals(Matrix22.IDENTITY, a.times(a.invert()))
    }
}

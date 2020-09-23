import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.openrndr.extra.noise.random
import org.openrndr.panel.elements.round
import kotlin.random.Random.Default

@Serializable
sealed class DoubleOrRandom {
    @Serializable
    data class Constant(override val value: Double) : DoubleOrRandom() {
        override fun toString(): String = value.round(3).toString()
    }

    @Serializable
    data class Random(val min: Double, val max: Double) : DoubleOrRandom() {
        @Transient
        override val value: Double = random(min, max)

        override fun toString(): String = value.round(3).toString()
    }

    abstract val value: Double
}

@Serializable
sealed class IntOrRandom {
    @Serializable
    data class Constant(override val value: Int) : IntOrRandom() {
        override fun toString(): String = value.toString()
    }

    @Serializable
    data class Random(val min: Int, val max: Int) : IntOrRandom() {
        @Transient
        override val value: Int = Default.nextInt(min, max + 1)

        override fun toString(): String = value.toString()
    }

    abstract val value: Int
}

// Addition
operator fun Double.plus(other: DoubleOrRandom) = this + other.value
operator fun Double.plus(other: IntOrRandom) = this + other.value
operator fun DoubleOrRandom.plus(other: Double) = this.value + other
operator fun DoubleOrRandom.plus(other: DoubleOrRandom) =
    this.value + other.value

operator fun DoubleOrRandom.plus(other: Int) = this.value + other
operator fun DoubleOrRandom.plus(other: IntOrRandom) = this.value + other.value
operator fun Int.plus(other: DoubleOrRandom) = this + other.value
operator fun Int.plus(other: IntOrRandom) = this + other.value
operator fun IntOrRandom.plus(other: Double) = this.value + other
operator fun IntOrRandom.plus(other: DoubleOrRandom) = this.value + other.value
operator fun IntOrRandom.plus(other: Int) = this.value + other
operator fun IntOrRandom.plus(other: IntOrRandom) = this.value + other.value

// Subtraction
operator fun Double.minus(other: DoubleOrRandom) = this - other.value
operator fun Double.minus(other: IntOrRandom) = this - other.value
operator fun DoubleOrRandom.minus(other: Double) = this.value - other
operator fun DoubleOrRandom.minus(other: DoubleOrRandom) =
    this.value - other.value

operator fun DoubleOrRandom.minus(other: Int) = this.value - other
operator fun DoubleOrRandom.minus(other: IntOrRandom) = this.value - other.value
operator fun Int.minus(other: DoubleOrRandom) = this - other.value
operator fun Int.minus(other: IntOrRandom) = this - other.value
operator fun IntOrRandom.minus(other: Double) = this.value - other
operator fun IntOrRandom.minus(other: DoubleOrRandom) = this.value - other.value
operator fun IntOrRandom.minus(other: Int) = this.value - other
operator fun IntOrRandom.minus(other: IntOrRandom) = this.value - other.value

// Multiplication
operator fun Double.times(other: DoubleOrRandom) = this * other.value
operator fun Double.times(other: IntOrRandom) = this * other.value
operator fun DoubleOrRandom.times(other: Double) = this.value * other
operator fun DoubleOrRandom.times(other: DoubleOrRandom) =
    this.value * other.value

operator fun DoubleOrRandom.times(other: Int) = this.value * other
operator fun DoubleOrRandom.times(other: IntOrRandom) = this.value * other.value
operator fun Int.times(other: DoubleOrRandom) = this * other.value
operator fun Int.times(other: IntOrRandom) = this * other.value
operator fun IntOrRandom.times(other: Double) = this.value * other
operator fun IntOrRandom.times(other: DoubleOrRandom) = this.value * other.value
operator fun IntOrRandom.times(other: Int) = this.value * other
operator fun IntOrRandom.times(other: IntOrRandom) = this.value * other.value

// Division
operator fun Double.div(other: DoubleOrRandom) = this / other.value
operator fun Double.div(other: IntOrRandom) = this / other.value
operator fun DoubleOrRandom.div(other: Double) = this.value / other
operator fun DoubleOrRandom.div(other: DoubleOrRandom) =
    this.value / other.value

operator fun DoubleOrRandom.div(other: Int) = this.value / other
operator fun DoubleOrRandom.div(other: IntOrRandom) = this.value / other.value
operator fun Int.div(other: DoubleOrRandom) = this / other.value
operator fun Int.div(other: IntOrRandom) = this / other.value
operator fun IntOrRandom.div(other: Double) = this.value / other
operator fun IntOrRandom.div(other: DoubleOrRandom) = this.value / other.value
operator fun IntOrRandom.div(other: Int) = this.value / other
operator fun IntOrRandom.div(other: IntOrRandom) = this.value / other.value

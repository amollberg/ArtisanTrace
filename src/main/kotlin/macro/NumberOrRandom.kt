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
        override val value: Int = Default.nextInt(min, max)

        override fun toString(): String = value.toString()
    }

    abstract val value: Int
}


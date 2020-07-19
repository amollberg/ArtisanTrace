import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.math.saturate
import org.openrndr.shape.Color
import org.openrndr.shape.Composition
import org.openrndr.svg.loadSVG
import java.io.File
import kotlin.math.round

data class InferredInterface(val ends: List<Vector2>, val terminalCount: Int)

data class Svg(
    var composition: Composition? = null,
    override var backingFile: File
) : FileBacked {

    val interfaces = inferredInterfaces(composition)

    companion object {
        fun fromFile(file: File): Svg {
            var svgText = file.readText()
            svgText = "(\\d)px;".toRegex().replace(svgText, "\\1;")
            val svg = Svg(loadSVG(svgText), file)
            svg.hideInterfaceShapes()
            return svg
        }

        private fun inferredInterfaces(composition: Composition?):
                List<InferredInterface> {
            if (composition == null) return emptyList()
            return composition.findShapes()
                .filter { sameRGB(it.effectiveStroke, FUCHSIA) }
                .filter { it.effectiveStroke?.let { it.a > 0.0 } ?: false }
                .flatMap { shapeNode ->
                    shapeNode.shape.contours.flatMap {
                        it.segments.map {
                            InferredInterface(
                                listOf(it.start, it.end),
                                colorToTerminalCount(shapeNode.effectiveStroke)
                            )
                        }
                    }
                }
        }
    }

    fun hideInterfaceShapes() {
        val c = composition ?: return
        return c.findShapes()
            .filter { sameRGB(it.effectiveStroke, FUCHSIA) }
            .forEach {
                // Make the shape stroke invisible
                it.stroke = Color(FUCHSIA.opacify(0.0))
            }
    }
}

fun colorToTerminalCount(color: ColorRGBa?) =
    color?.ifPresent {
        1 + round(255 * (1 - color.a.saturate())).toInt()
    } ?: 0

/** Does not take alpha into account */
fun sameRGB(a: ColorRGBa?, b: ColorRGBa?) =
    listOf(
        (a ?: ColorRGBa.TRANSPARENT),
        (b ?: ColorRGBa.TRANSPARENT)
    ).let { (a, b) ->
        listOf(a.r, a.g, a.b) == listOf(b.r, b.g, b.b)
    }

val FUCHSIA = ColorRGBa.fromHex(0xff00ff)
val INTERFACE_KEY_COLOR = FUCHSIA

fun interfaceKeyColor(terminalCount: Int) =
    INTERFACE_KEY_COLOR.copy(a = (256 - terminalCount).toDouble() / 255)

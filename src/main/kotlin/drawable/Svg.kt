import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.Color
import org.openrndr.shape.Composition
import org.openrndr.svg.loadSVG
import java.io.File

data class Svg(
    var composition: Composition? = null, override var
    backingFile: File
) : FileBacked {

    companion object {
        fun fromFile(file: File): Svg {
            var svgText = file.readText()
            svgText = "(\\d)px;".toRegex().replace(svgText, "\\1;")
            val svg = Svg(loadSVG(svgText), file)
            svg.hideInterfaceShapes()
            return svg
        }
    }

    val interfaceEnds: List<List<Vector2>>
        get() {
            val c = composition ?: return listOf()
            return c.findShapes()
                .filter { sameRGB(it.effectiveStroke, FUCHSIA) }
                .flatMap {
                    it.shape.contours.flatMap {
                        it.segments.map {
                            listOf(it.start, it.end)
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

/** Does not take alpha into account */
fun sameRGB(a: ColorRGBa?, b: ColorRGBa?) =
    listOf(
        (a ?: ColorRGBa.TRANSPARENT),
        (b ?: ColorRGBa.TRANSPARENT)
    ).let { (a, b) ->
        listOf(a.r, a.g, a.b) == listOf(b.r, b.g, b.b)
    }

val FUCHSIA = ColorRGBa.fromHex(0xff00ff)

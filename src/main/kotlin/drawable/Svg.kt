import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import org.openrndr.shape.Composition
import org.openrndr.svg.loadSVG
import java.io.File

data class Svg(
    var composition: Composition? = null, override var
    backingFile: File
) : FileBacked {

    companion object {
        fun fromFile(file: File): Svg {
            val svgText = file.readText()
            return Svg(loadSVG(svgText), file)
        }
    }

    val interfaceEnds: List<List<Vector2>>
        get() {
            val c = composition ?: return listOf()
            return c.findShapes()
                .filter { sameHex(it.effectiveStroke, FUCHSIA) }
                .flatMap {
                    it.shape.contours.flatMap {
                        it.segments.map {
                            listOf(it.start, it.end)
                        }
                    }
                }
        }
}

fun sameHex(a: ColorRGBa?, b: ColorRGBa?) =
    listOf(
        (a ?: ColorRGBa.TRANSPARENT),
        (b ?: ColorRGBa.TRANSPARENT)
    ).let { (a, b) ->
        listOf(a.r, a.g, a.b, a.a) == listOf(b.r, b.g, b.b, b.a)
    }

val FUCHSIA = ColorRGBa.fromHex(0xff00ff)

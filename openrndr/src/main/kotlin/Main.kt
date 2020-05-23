import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.Drawer
import org.openrndr.draw.loadFont
import org.openrndr.draw.loadImage
import org.openrndr.draw.tint
import org.openrndr.shape.SegmentJoin
import org.openrndr.shape.SegmentJoin.MITER
import org.openrndr.shape.ShapeContour
import org.openrndr.shape.contour
import kotlin.math.cos
import kotlin.math.sin

fun offsetPoly(drawer: Drawer) {
    val c = contour {
        moveTo(100.0, 100.0)
        lineTo(200.0, 150.0)
        lineTo(70.0, 180.0)
        lineTo(100.0, 120.0)
    }
    drawer.contour(c)
    drawer.contour(c.offset(15.0, joinType = MITER))
    drawer.contour(c.offset(-15.0, joinType = MITER))
}

fun main() = application {
    configure {
        width = 768
        height = 576
    }

    program {
        val image = loadImage("data/images/pm5544.png")
        val font = loadFont("data/fonts/IBMPlexMono-Regular.ttf", 64.0)

        extend {
            drawer.fill = ColorRGBa.WHITE
            drawer.stroke = ColorRGBa.PINK
            drawer.strokeWeight = 2.0
            offsetPoly(drawer)
        }
    }
}
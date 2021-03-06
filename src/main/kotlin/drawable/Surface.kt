import org.openrndr.color.ColorRGBa.Companion.BLUE
import org.openrndr.color.ColorRGBa.Companion.GREEN
import org.openrndr.color.ColorRGBa.Companion.TRANSPARENT
import org.openrndr.color.ColorRGBa.Companion.WHITE
import org.openrndr.color.ColorRGBa.Companion.fromHex

data class Surface(val poly: Poly, val possibleInterfaces: Set<Interface>) {
    val interfaces: Set<Interface>
        get() = possibleInterfaces
            .filter { itf ->
                interfaceIsOnSurface(itf)
            }.toSet()

    val concaveAreas: List<Surface>
        get() =
            poly.concaveAreas.map {
                Surface(it, possibleInterfaces)
            }

    fun draw(drawer: OrientedDrawer) {
        isolatedStyle(
            drawer.drawer,
            stroke = fromHex(0xFF9040),
            fill = TRANSPARENT,
            strokeWeight = 2.0
        ) {
            poly.draw(drawer)
        }
        isolatedStyle(
            drawer.drawer,
            stroke = GREEN,
            fill = TRANSPARENT,
            strokeWeight = 3.0
        ) {
            poly.segmentsOnConvexHull.forEach {
                drawer.drawer.lineSegment(it.lineSegment(drawer.system))
            }
        }
        isolatedStyle(
            drawer.drawer,
            fill = fromHex(0x7F0000),
            stroke = WHITE
        ) {
            poly.concaveAreas.forEach {
                it.draw(drawer)
            }
        }
        isolatedStyle(
            drawer.drawer, stroke = BLUE
        ) {
            interfaces.forEach {
                it.draw(drawer)
            }
        }
    }

    private fun interfaceIsOnSurface(itf: Interface) =
        poly.segmentPointers.any {
            it.segment(itf.center.system)
                .project(itf.center.xy())
                .distance == 0.0
        }
}

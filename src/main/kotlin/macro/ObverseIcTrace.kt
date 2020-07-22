import org.openrndr.math.Vector2
import org.openrndr.shape.Segment
import java.io.File
import java.lang.Math.floorMod
import kotlin.math.abs
import kotlin.math.sqrt

fun SketchMacro.ObverseIcTrace.create(model: Model) {
    val viaFile = File("src/test/resources/Via3.svg")
    val viaRadius = 5.0
    val height = (pinsPerSide - 1) * pinPitch
    val leftSide = Segment(Vector2.ZERO, Vector2(0.0, height))
    val rightSide = Segment(Vector2(width, 0.0), Vector2(width, height))

    val outerInterface = Interface(
        model.system.coord(rightSide.position(0.5) + Vector2(height / 2, 0.0)),
        90.0,
        height / 2,
        pinsPerSide * 2 - floorMod(pinsPerSide, 2)
    )
    model.interfaces.add(outerInterface)

    fun createTerminal(center: Vector2): Terminals {
        val itf = Interface(model.system.coord(center), 90.0, 5.0, 1)
        model.interfaces.add(itf)
        return itf.getTerminals()
    }

    fun addSvg(position: Vector2): SvgComponent {
        val svgComponent = model.loadSvg(viaFile, model.system.coord(position))
        model.addSvg(svgComponent)
        return svgComponent
    }

    val indexCenter = (pinsPerSide - 1) / 2.0
    val leftPositions = leftSide.equidistantPositions(pinsPerSide)
    val rightPositions = rightSide.equidistantPositions(pinsPerSide)

    val rowPairs = 0 until pinsPerSide / 2
    rowPairs.forEachIndexed { i, _ ->
        val upperIndex = i
        val lowerIndex = pinsPerSide - 1 - i

        if (abs(upperIndex - indexCenter) > 0) {
            // Add upper right pin and trace
            val upperRightSvg = addSvg(rightPositions[upperIndex])
            model.traces.add(trace(model.system) {
                reverseKnee(true)
                terminals(upperRightSvg.interfaces.first().getTerminals())
                terminals(Terminals(outerInterface, upperIndex * 2))
            })
        }
        // Add upper left pin and trace
        val upperLeftSvg = addSvg(leftPositions[upperIndex])
        val upperWaypoint =
            (rightPositions[upperIndex] + rightPositions[upperIndex + 1]) * 0.5 +
                    Vector2(viaRadius / sqrt(2.0), 0.0)
        model.traces.add(trace(model.system) {
            reverseKnee(true)
            terminals(upperLeftSvg.interfaces.first().getTerminals())
            if (abs(upperIndex - indexCenter) > 1)
                terminals(createTerminal(upperWaypoint))
            terminals(Terminals(outerInterface, upperIndex * 2 + 1))
        })

        // Add lower right pin and trace
        if (abs(lowerIndex - indexCenter) > 0) {
            val lowerRightSvg = addSvg(rightPositions[lowerIndex])
            val lowerRightPin = outerInterface.terminalCount - 1 - i * 2
            model.traces.add(trace(model.system) {
                reverseKnee(true)
                terminals(lowerRightSvg.interfaces.first().getTerminals())
                terminals(Terminals(outerInterface, lowerRightPin))
            })
        }

        // Add lower left pin and trace
        val lowerLeftSvg = addSvg(leftPositions[lowerIndex])
        val lowerLeftPin = outerInterface.terminalCount - 2 - i * 2
        val lowerWaypoint =
            (rightPositions[lowerIndex - 1] + rightPositions[lowerIndex]) * 0.5 +
                    Vector2(viaRadius / sqrt(2.0), 0.0)
        model.traces.add(trace(model.system) {
            reverseKnee(true)
            terminals(lowerLeftSvg.interfaces.first().getTerminals())
            if (abs(upperIndex - indexCenter) > 1)
                terminals(createTerminal(lowerWaypoint))
            terminals(Terminals(outerInterface, lowerLeftPin))
        })
    }

    // Add middle left pin, if not already added
    if (floorMod(pinsPerSide, 2) == 1) {
        val middleSvg = addSvg(leftPositions[(pinsPerSide - 1) / 2])
        val middlePin = (outerInterface.terminalCount - 1) / 2
        model.traces.add(trace(model.system) {
            terminals(middleSvg.interfaces.first().getTerminals())
            terminals(Terminals(outerInterface, middlePin))
        })
    }
}

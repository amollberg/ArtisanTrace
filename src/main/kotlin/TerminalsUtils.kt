import org.openrndr.math.Vector2

/** Compute the position closest to the given position that is also
 *  located on an imagined line orthogonal from the terminal line and
 *  intersecting in the center of the terminal range.
 */
fun projectOrthogonal(position: Vector2, terminals: Terminals):
        Vector2 {
    val center = getCenter(terminals)
    val (end1, end2) = terminals.hostInterface.getEnds()
    val terminalLine = end2 - end1
    if (terminalLine.length == 0.0) {
        return position
    }
    val rel = position - center
    val onLine = center + terminalLine *
            (rel.dot(terminalLine) / terminalLine.squaredLength)
    return position - (onLine - center)
}

fun getCenter(terminals: Terminals): Vector2 {
    val count = terminals.count()
    return when (count % 2 == 0) {
        // Even number of terminals, take average of the middle two
        true -> (positionOf(terminals, count / 2) +
                positionOf(terminals, count / 2 - 1)) * 0.5
        // Odd number of terminals, take position of the middle one
        false -> positionOf(terminals, (count - 1) / 2)
    }
}

private fun positionOf(terminals: Terminals, index: Int) =
    terminals.hostInterface.getTerminalPosition(
        terminals.range.elementAt(index))

import coordinates.Coordinate

/** Compute the position closest to the given position that is also
 *  located on an imagined line orthogonal from the terminal line and
 *  intersecting in the center of the terminal range.
 */
fun projectOrthogonal(position: Coordinate, terminals: Terminals):
        Coordinate {
    val system = position.system
    val pos = position.xyIn(system)
    val center = getCenter(terminals).xyIn(system)
    val (end1, end2) = terminals.hostInterface.getEnds()
    val terminalLine = (end2 - end1).xyIn(system)
    if (terminalLine.length == 0.0) {
        return position
    }
    val rel = pos - center
    val onLine = center + terminalLine *
            (rel.dot(terminalLine) / terminalLine.squaredLength)
    val projected = pos - (onLine - center)
    return Coordinate(projected, system)
}

fun getCenter(terminals: Terminals): Coordinate {
    val count = terminals.count
    return when (count % 2 == 0) {
        // Even number of terminals, take average of the middle two
        true -> positionOf(terminals, count / 2).lerp(
            0.5,
            positionOf(terminals, count / 2 - 1)
        )
        // Odd number of terminals, take position of the middle one
        false -> positionOf(terminals, (count - 1) / 2)
    }
}

private fun positionOf(terminals: Terminals, index: Int) =
    terminals.hostInterface.getTerminalPosition(
        terminals.range.elementAt(index)
    )

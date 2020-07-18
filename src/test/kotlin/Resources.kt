import org.openrndr.math.Vector2

/** The terminal count and position of the start and end of each inferred
 * interface in the SVG component. Relative to SVG component local coordinate
 * system.
 */
val EXPECTED_INTERFACES = hashMapOf(
    "src/test/resources/IC1.svg" to listOf(
        InferredInterface(listOf(Vector2(8.0, 10.0), Vector2(8.0, 25.0)), 6),
        InferredInterface(listOf(Vector2(22.0, 10.0), Vector2(22.0, 25.0)), 6)
    ),
    "src/test/resources/Via2.svg" to listOf(
        InferredInterface(
            listOf(
                Vector2(0.63883413, 10.956321), Vector2(-0.63883413, 10.956321)
            ),
            1
        )
    ),
    "src/test/resources/Via3.svg" to listOf(
        InferredInterface(
            listOf(Vector2(0.63883413, 0.0), Vector2(-0.63883413, 0.0)),
            1
        )
    )
)

/** The offset down and to the right to a point inside the bounds of the
 *  component. Relative to component local coordinate system.
 */
val OFFSET_TO_BOUNDING_BOX = hashMapOf(
    "src/test/resources/IC1.svg" to Vector2(18.0, 18.0),
    "src/test/resources/IC1.ats" to Vector2(18.0, 18.0)
)

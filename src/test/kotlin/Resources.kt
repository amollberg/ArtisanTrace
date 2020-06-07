import org.openrndr.math.Vector2

/** The position of the start and end of each inferred interface in the SVG
 * component. Relative to SVg component local coordinate system. */
val EXPECTED_INTERFACE_ENDS = hashMapOf(
    "src/test/resources/IC1.svg" to listOf(
        listOf(Vector2(8.0, 10.0), Vector2(8.0, 25.0)),
        listOf(Vector2(22.0, 10.0), Vector2(22.0, 25.0))
    )
)

/** The offset down and to the right to a point inside the bounds of the
 *  SVG component. Relative to SVG component local coordinate system.
 */
val OFFSET_TO_BOUNDING_BOX = hashMapOf(
    "src/test/resources/IC1.svg" to Vector2(18.0, 18.0)
)
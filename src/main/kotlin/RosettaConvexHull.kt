// Copyright (c)  2020  http://rosettacode.org.
// Permission is granted to copy, distribute and/or modify this document
// under the terms of the GNU Free Documentation License, Version 1.2
// or any later version published by the Free Software Foundation;
// with no Invariant Sections, no Front-Cover Texts, and no Back-Cover
// Texts.  A copy of the license is included in the section entitled "GNU
// Free Documentation License".

// version 1.1.3

class Point(val x: Double, val y: Double) : Comparable<Point> {

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    override fun compareTo(other: Point) = this.x.compareTo(other.x)

    override fun toString() = "($x, $y)"
}

fun convexHull(p: Array<Point>): List<Point> {
    if (p.isEmpty()) return emptyList()
    p.sort()
    val h = mutableListOf<Point>()

    // lower hull
    for (pt in p) {
        while (h.size >= 2 && !ccw(h[h.size - 2], h.last(), pt)) {
            h.removeAt(h.lastIndex)
        }
        h.add(pt)
    }

    // upper hull
    val t = h.size + 1
    for (i in p.size - 2 downTo 0) {
        val pt = p[i]
        while (h.size >= t && !ccw(h[h.size - 2], h.last(), pt)) {
            h.removeAt(h.lastIndex)
        }
        h.add(pt)
    }

    h.removeAt(h.lastIndex)
    return h
}

/* ccw returns true if the three points make a counter-clockwise turn */
fun ccw(a: Point, b: Point, c: Point) =
    ((b.x - a.x) * (c.y - a.y)) > ((b.y - a.y) * (c.x - a.x))

fun main(args: Array<String>) {
}

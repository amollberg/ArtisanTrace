// Move the first element to the last place in the list
fun <T> rotateFront(l: List<T>): List<T> =
    if (l.size == 1) l
    else l.drop(1) + l[0]

// Return all combinations of elements from a and from b
fun <T> crossProduct(a: Iterable<T>, b: Iterable<T>) =
    a.flatMap { ae ->
        b.map { be -> Pair<T, T>(ae, be) }
    }

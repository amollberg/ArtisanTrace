// Move the first element to the last place in the list
fun <T> rotateFront(l: List<T>): List<T> =
    if (l.size == 1) l
    else l.drop(1) + l[0]

/** Stringify and flatten to something suitable in a file name */
fun dataClassToFileName(ob: Any) =
    ob.toString()
        .replace("(", "_")
        .replace(")", "")
        .replace(" ", "_")
        .replace("=", "")
        .filter { "$it".matches(Regex("([A-Za-z0-9_.])")) }

inline fun <T : Any> T.ifPresent(func: (t: T) -> Unit): Unit = func(this)

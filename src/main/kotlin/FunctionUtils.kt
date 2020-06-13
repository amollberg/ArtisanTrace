inline fun <T : Any, R : Any> T.ifPresent(func: (t: T) -> R): R = func(this)

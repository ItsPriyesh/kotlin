package foo

trait A {
    companion object {
        val OK: String = "OK"
    }
}

fun box(): String {
    return A.OK
}
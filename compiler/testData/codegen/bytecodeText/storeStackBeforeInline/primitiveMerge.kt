fun bar() : Boolean = true

fun foobar(x: Boolean, y: String, z: String) = x.toString() + y + z

inline fun foo() = "-"

fun test() {
    val result = foobar(if (1 == 1) true else bar(), foo(), "OK")
}

// 1 ISTORE
// 2 ILOAD
// 2 ASTORE
// 5 ALOAD
// 2 MAXLOCALS = 3
// 0 InlineMarker

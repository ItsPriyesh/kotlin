package foo

// NOTE THIS FILE IS AUTO-GENERATED by the generateTestDataForReservedWords.kt. DO NOT EDIT!

interface Trait {
    val arguments: Int
}

class TraitImpl : Trait {
    override val arguments: Int = 0
}

class TestDelegate : Trait by TraitImpl() {
    fun test() {
        testNotRenamed("arguments", { arguments })
    }
}

fun box(): String {
    TestDelegate().test()

    return "OK"
}
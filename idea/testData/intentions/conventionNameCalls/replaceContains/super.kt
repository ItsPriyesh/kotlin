// IS_APPLICABLE: false

open class Base {
    open fun contains(s: String) = true
}

class C : Base() {
    override fun contains(s: String): Boolean {
        return super.<caret>contains(s)
    }
}

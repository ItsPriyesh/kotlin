// This test was adapted from compiler/testData/codegen/boxWithStdlib/callableReference/function/local/.
package foo

fun box(): String {
    fun Int.is42With(that: Int) = this + 2 * that == 42
    return if ((Int::is42With)(16, 13)) "OK" else "Fail"
}

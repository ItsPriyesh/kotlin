import kotlin.reflect.KProperty

class Delegate {
    fun getValue(t: Any?, p: KProperty<*>): Int = 1
}

class A {
    val prop: Int by Delegate()
}

fun box(): String {
  return if(A().prop == 1) "OK" else "fail"
}

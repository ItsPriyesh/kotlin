import java.io.Serializable

interface Ok1 {
    fun <T> foo(t: T) where T : Cloneable, T : Serializable
    fun <T> foo(t: T) where T : Serializable, T : Cloneable
}


interface I1
interface I2 : I1

interface Ok2 {
    fun <T> foo(t: T) where T : I1, T : I2
    fun <T> foo(t: T) where T : I2, T : I1
}

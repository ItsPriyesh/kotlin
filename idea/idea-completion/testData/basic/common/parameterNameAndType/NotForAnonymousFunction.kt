package pack

class Boo

fun f() {
    x(fun (b<caret>))
}

// ABSENT: boo: Boo

package kotlin


//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//
// Generated from input file: src/kotlin/Iterables.kt
//


import java.util.*

/**
 * Returns *true* if all elements match the given *predicate*
 *
 * @includeFunctionBody ../../test/CollectionTest.kt all
 */
public inline fun <T> Array<T>.all(predicate: (T) -> Boolean) : Boolean {
    for (element in this) if (!predicate(element)) return false
    return true
}

/**
 * Returns *true* if any elements match the given *predicate*
 *
 * @includeFunctionBody ../../test/CollectionTest.kt any
 */
public inline fun <T> Array<T>.any(predicate: (T) -> Boolean) : Boolean {
    for (element in this) if (predicate(element)) return true
    return false
}

/**
 * Appends the string from all the elements separated using the *separator* and using the given *prefix* and *postfix* if supplied
 *
 * If a collection could be huge you can specify a non-negative value of *limit* which will only show a subset of the collection then it will
 * a special *truncated* separator (which defaults to "..."
 *
 * @includeFunctionBody ../../test/CollectionTest.kt appendString
 */
public inline fun <T> Array<T>.appendString(buffer: Appendable, separator: String = ", ", prefix: String = "", postfix: String = "", limit: Int = -1, truncated: String = "..."): Unit {
    buffer.append(prefix)
    var count = 0
    for (element in this) {
        if (++count > 1) buffer.append(separator)
        if (limit < 0 || count <= limit) {
            val text = if (element == null) "null" else element.toString()
            buffer.append(text)
        } else break
    }
    if (limit >= 0 && count > limit) buffer.append(truncated)
    buffer.append(postfix)
}

/**
 * Returns the number of elements which match the given *predicate*
 *
 * @includeFunctionBody ../../test/CollectionTest.kt count
 */
public inline fun <T> Array<T>.count(predicate: (T) -> Boolean) : Int {
    var count = 0
    for (element in this) if (predicate(element)) count++
    return count
}

/**
 * Returns the first element which matches the given *predicate* or *null* if none matched
 *
 * @includeFunctionBody ../../test/CollectionTest.kt find
 */
public inline fun <T> Array<T>.find(predicate: (T) -> Boolean) : T? {
    for (element in this) if (predicate(element)) return element
    return null
}

/**
 * Filters all elements which match the given predicate into the given list
 *
 * @includeFunctionBody ../../test/CollectionTest.kt filterIntoLinkedList
 */
public inline fun <T, C: MutableCollection<in T>> Array<T>.filterTo(result: C, predicate: (T) -> Boolean) : C {
    for (element in this) if (predicate(element)) result.add(element)
    return result
}

/**
 * Partitions this collection into a pair of collection
 *
 * @includeFunctionBody ../../test/CollectionTest.kt partition
 */
public inline fun <T> Array<T>.partition(predicate: (T) -> Boolean) : Pair<List<T>, List<T>> {
    val first = ArrayList<T>()
    val second = ArrayList<T>()
    for (element in this) {
        if (predicate(element)) {
            first.add(element)
        } else {
            second.add(element)
        }
    }
    return Pair(first, second)
}

/**
 * Returns a list containing all elements which do not match the given *predicate*
 *
 * @includeFunctionBody ../../test/CollectionTest.kt filterNotIntoLinkedList
 */
public inline fun <T, C: MutableCollection<in T>> Array<T>.filterNotTo(result: C, predicate: (T) -> Boolean) : C {
    for (element in this) if (!predicate(element)) result.add(element)
    return result
}

/**
 * Filters all non-*null* elements into the given list
 *
 * @includeFunctionBody ../../test/CollectionTest.kt filterNotNullIntoLinkedList
 */
public inline fun <T, C: MutableCollection<in T>> Array<T?>?.filterNotNullTo(result: C) : C {
    if (this != null) {
        for (element in this) if (element != null) result.add(element)
    }
    return result
}

/**
 * Returns the result of transforming each element to one or more values which are concatenated together into a single list
 *
 * @includeFunctionBody ../../test/CollectionTest.kt flatMap
 */
public inline fun <T, R> Array<T>.flatMapTo(result: MutableCollection<R>, transform: (T) -> Collection<R>) : Collection<R> {
    for (element in this) {
        val list = transform(element)
        if (list != null) {
            for (r in list) result.add(r)
        }
    }
    return result
}

/**
 * Performs the given *operation* on each element
 *
 * @includeFunctionBody ../../test/CollectionTest.kt forEach
 */
public inline fun <T> Array<T>.forEach(operation: (T) -> Unit) : Unit = for (element in this) operation(element)

/**
 * Folds all elements from from left to right with the *initial* value to perform the operation on sequential pairs of elements
 *
 * @includeFunctionBody ../../test/CollectionTest.kt fold
 */
public inline fun <T> Array<T>.fold(initial: T, operation: (T, T) -> T): T {
    var answer = initial
    for (element in this) answer = operation(answer, element)
    return answer
}

/**
 * Folds all elements from right to left with the *initial* value to perform the operation on sequential pairs of elements
 *
 * @includeFunctionBody ../../test/CollectionTest.kt foldRight
 */
public inline fun <T> Array<T>.foldRight(initial: T, operation: (T, T) -> T): T = reverse().fold(initial, {x, y -> operation(y, x)})


/**
 * Applies binary operation to all elements of iterable, going from left to right.
 * Similar to fold function, but uses the first element as initial value
 *
 * @includeFunctionBody ../../test/CollectionTest.kt reduce
 */
public inline fun <T> Array<T>.reduce(operation: (T, T) -> T): T {
    val iterator = this.iterator().sure()
    if (!iterator.hasNext()) {
        throw UnsupportedOperationException("Empty iterable can't be reduced")
    }

    var result: T = iterator.next() //compiler doesn't understand that result will initialized anyway
    while (iterator.hasNext()) {
        result = operation(result, iterator.next())
    }

    return result
}

/**
 * Applies binary operation to all elements of iterable, going from right to left.
 * Similar to foldRight function, but uses the last element as initial value
 *
 * @includeFunctionBody ../../test/CollectionTest.kt reduceRight
 */
public inline fun <T> Array<T>.reduceRight(operation: (T, T) -> T): T = reverse().reduce { x, y -> operation(y, x) }


/**
 * Groups the elements in the collection into a new [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 *
 * @includeFunctionBody ../../test/CollectionTest.kt groupBy
 */
public inline fun <T, K> Array<T>.groupBy(toKey: (T) -> K) : Map<K, MutableList<T>> = groupByTo<T,K>(HashMap<K, MutableList<T>>(), toKey)

/**
 * Groups the elements in the collection into the given [[Map]] using the supplied *toKey* function to calculate the key to group the elements by
 *
 * @includeFunctionBody ../../test/CollectionTest.kt groupBy
 */
public inline fun <T, K> Array<T>.groupByTo(result: MutableMap<K, MutableList<T>>, toKey: (T) -> K) : Map<K, MutableList<T>> {
    for (element in this) {
        val key = toKey(element)
        val list = result.getOrPut(key) { ArrayList<T>() }
        list.add(element)
    }
    return result
}

/**
 * Creates a string from all the elements separated using the *separator* and using the given *prefix* and *postfix* if supplied.
 *
 * If a collection could be huge you can specify a non-negative value of *limit* which will only show a subset of the collection then it will
 * a special *truncated* separator (which defaults to "..."
 *
 * @includeFunctionBody ../../test/CollectionTest.kt makeString
 */
public inline fun <T> Array<T>.makeString(separator: String = ", ", prefix: String = "", postfix: String = "", limit: Int = -1, truncated: String = "..."): String {
    val buffer = StringBuilder()
    appendString(buffer, separator, prefix, postfix, limit, truncated)
    return buffer.toString().sure()
}

/** Returns a list containing the everything but the first elements that satisfy the given *predicate* */
public inline fun <T, L: MutableList<in T>> Array<T>.dropWhileTo(result: L, predicate: (T) -> Boolean) : L {
    var start = true
    for (element in this) {
        if (start && predicate(element)) {
            // ignore
        } else {
            start = false
            result.add(element)
        }
    }
    return result
}

/** Returns a list containing the first elements that satisfy the given *predicate* */
public inline fun <T, C: MutableCollection<in T>> Array<T>.takeWhileTo(result: C, predicate: (T) -> Boolean) : C {
    for (element in this) if (predicate(element)) result.add(element) else break
    return result
}

/** Copies all elements into the given collection */
public inline fun <in T, C: MutableCollection<in T>> Array<T>.toCollection(result: C) : C {
    for (element in this) result.add(element)
    return result
}

/**
 * Reverses the order the elements into a list
 *
 * @includeFunctionBody ../../test/CollectionTest.kt reverse
 */
public inline fun <T> Array<T>.reverse() : List<T> {
    val list = toCollection(ArrayList<T>())
    Collections.reverse(list)
    return list
}

/** Copies all elements into a [[LinkedList]]  */
public inline fun <in T> Array<T>.toLinkedList() : LinkedList<T> = toCollection(LinkedList<T>())

/** Copies all elements into a [[List]] */
public inline fun <in T> Array<T>.toList() : List<T> = toCollection(ArrayList<T>())

/** Copies all elements into a [[List] */
public inline fun <in T> Array<T>.toCollection() : Collection<T> = toCollection(ArrayList<T>())

/** Copies all elements into a [[Set]] */
public inline fun <in T> Array<T>.toSet() : Set<T> = toCollection(HashSet<T>())

/**
  TODO figure out necessary variance/generics ninja stuff... :)
public inline fun <in T> Array<T>.toSortedList(transform: fun(T) : java.lang.Comparable<*>) : List<T> {
    val answer = this.toList()
    answer.sort(transform)
    return answer
}
*/

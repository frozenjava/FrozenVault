package net.frozendevelopment.frozenvault.extensions

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend fun <A, B> Iterable<A>.parallelMap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

suspend inline fun <T> Iterable<T>.parallelProcess(crossinline f: suspend (T) -> Unit) = coroutineScope {
    map { async { f(it) } }.awaitAll()
}

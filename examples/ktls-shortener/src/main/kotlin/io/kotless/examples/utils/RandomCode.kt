package io.kotless.examples.utils

import java.math.BigInteger
import java.security.SecureRandom

object RandomCode {
    private val rnd = SecureRandom()
    private const val defaultLength: Int = 6

    /** It is highly recommended to use default 36 radix **/
    fun next(len: Int = defaultLength, radix: Int = 36): String {
        return BigInteger(128, rnd).toString(radix).takeLast(len)
    }

    fun nextBytes(len: Int): ByteArray = ByteArray(len).apply {
        rnd.nextBytes(this)
    }
}

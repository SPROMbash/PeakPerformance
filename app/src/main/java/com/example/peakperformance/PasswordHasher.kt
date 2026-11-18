package com.example.peakperformance

import android.util.Base64
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Hashing de contraseñas con PBKDF2 y sal aleatoria por usuario.
 *
 * Formato almacenado: "pbkdf2$<algoritmo>$<iteraciones>$<sal base64>$<hash base64>"
 * Las contraseñas nunca se guardan en texto plano.
 */
object PasswordHasher {
    private const val PREFIX = "pbkdf2"
    private const val ITERATIONS = 120_000
    private const val SALT_BYTES = 16
    private const val KEY_BITS = 256

    // PBKDF2WithHmacSHA256 está disponible desde API 26; en API 24-25 se usa SHA1.
    private val defaultAlgorithm: String by lazy {
        try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            "PBKDF2WithHmacSHA256"
        } catch (e: NoSuchAlgorithmException) {
            "PBKDF2WithHmacSHA1"
        }
    }

    fun hash(password: String): String {
        val salt = ByteArray(SALT_BYTES).also { SecureRandom().nextBytes(it) }
        val hash = derive(password, salt, ITERATIONS, defaultAlgorithm)
        return listOf(PREFIX, defaultAlgorithm, ITERATIONS.toString(), encode(salt), encode(hash))
            .joinToString("$")
    }

    fun verify(password: String, stored: String): Boolean {
        val parts = stored.split("$")
        if (parts.size != 5 || parts[0] != PREFIX) return false
        val iterations = parts[2].toIntOrNull() ?: return false
        val salt = decode(parts[3])
        val expected = decode(parts[4])
        val actual = derive(password, salt, iterations, parts[1])
        // Comparación en tiempo constante
        return MessageDigest.isEqual(expected, actual)
    }

    /** true si el valor almacenado ya es un hash (y no una contraseña antigua en texto plano). */
    fun isHashed(stored: String): Boolean = stored.startsWith("$PREFIX$")

    private fun derive(password: String, salt: ByteArray, iterations: Int, algorithm: String): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_BITS)
        try {
            return SecretKeyFactory.getInstance(algorithm).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray) = Base64.encodeToString(bytes, Base64.NO_WRAP)
    private fun decode(text: String) = Base64.decode(text, Base64.NO_WRAP)
}

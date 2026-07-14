package com.example.arcshiftwelding.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Utilidades de seguridad locales para contraseñas y códigos de respaldo.
 *
 * Las contraseñas se almacenan con PBKDF2-HMAC-SHA256 y salt aleatorio.
 * También se mantiene compatibilidad temporal con contraseñas antiguas en texto plano,
 * que se actualizan automáticamente al iniciar sesión correctamente.
 */
object PasswordSecurity {

    private const val PREFIJO_PASSWORD = "pbkdf2_sha256"
    private const val ITERACIONES = 120_000
    private const val LONGITUD_CLAVE_BITS = 256
    private const val LONGITUD_SALT = 16
    private const val CANTIDAD_CODIGOS_RESPALDO = 10
    private const val LONGITUD_CODIGO_RESPALDO = 12
    private const val ALFABETO_CODIGOS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"

    private val secureRandom = SecureRandom()

    fun hashPassword(password: String): String {
        require(password.isNotBlank()) { "La contraseña no puede estar vacía" }

        val salt = ByteArray(LONGITUD_SALT).also(secureRandom::nextBytes)
        val hash = derivarClave(
            password = password,
            salt = salt,
            iteraciones = ITERACIONES
        )

        return listOf(
            PREFIJO_PASSWORD,
            ITERACIONES.toString(),
            Base64.getEncoder().withoutPadding().encodeToString(salt),
            Base64.getEncoder().withoutPadding().encodeToString(hash)
        ).joinToString("$")
    }

    fun verificarPassword(password: String, valorAlmacenado: String): Boolean {
        if (!esPasswordHasheado(valorAlmacenado)) {
            // Compatibilidad con registros antiguos. Después de un inicio exitoso,
            // LoginViewModel sustituye este valor por un hash PBKDF2.
            return MessageDigest.isEqual(
                password.toByteArray(Charsets.UTF_8),
                valorAlmacenado.toByteArray(Charsets.UTF_8)
            )
        }

        val partes = valorAlmacenado.split('$')
        if (partes.size != 4) return false

        return runCatching {
            val iteraciones = partes[1].toInt()
            val salt = Base64.getDecoder().decode(partes[2])
            val hashEsperado = Base64.getDecoder().decode(partes[3])
            val hashActual = derivarClave(password, salt, iteraciones)
            MessageDigest.isEqual(hashEsperado, hashActual)
        }.getOrDefault(false)
    }

    fun esPasswordHasheado(valor: String): Boolean =
        valor.startsWith("$PREFIJO_PASSWORD$")

    fun validarNuevaPassword(password: String): String? {
        return when {
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
            password.none(Char::isLetter) -> "La contraseña debe incluir al menos una letra."
            password.none(Char::isDigit) -> "La contraseña debe incluir al menos un número."
            else -> null
        }
    }

    fun generarCodigosRespaldo(): List<String> =
        List(CANTIDAD_CODIGOS_RESPALDO) {
            val contenido = buildString(LONGITUD_CODIGO_RESPALDO) {
                repeat(LONGITUD_CODIGO_RESPALDO) {
                    append(ALFABETO_CODIGOS[secureRandom.nextInt(ALFABETO_CODIGOS.length)])
                }
            }
            contenido.chunked(4).joinToString("-")
        }

    fun normalizarCodigoRespaldo(codigo: String): String =
        codigo.uppercase()
            .filter { it.isLetterOrDigit() }

    fun hashCodigoRespaldo(codigo: String): String {
        val normalizado = normalizarCodigoRespaldo(codigo)
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(normalizado.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().withoutPadding().encodeToString(digest)
    }

    private fun derivarClave(
        password: String,
        salt: ByteArray,
        iteraciones: Int
    ): ByteArray {
        val especificacion = PBEKeySpec(
            password.toCharArray(),
            salt,
            iteraciones,
            LONGITUD_CLAVE_BITS
        )

        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(especificacion)
                .encoded
        } finally {
            especificacion.clearPassword()
        }
    }
}

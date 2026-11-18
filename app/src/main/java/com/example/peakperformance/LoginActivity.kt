package com.example.peakperformance

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var admin: AdminSQLiteOpenHelper
    private lateinit var nombreUsuarioEditText: EditText
    private lateinit var contrasenaEditText: EditText
    private lateinit var botonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        admin = AdminSQLiteOpenHelper(this)

        nombreUsuarioEditText = findViewById(R.id.username)
        contrasenaEditText = findViewById(R.id.password)
        botonLogin = findViewById(R.id.loginButton)

        botonLogin.setOnClickListener {
            val nombreUsuario = nombreUsuarioEditText.text.toString()
            val contrasena = contrasenaEditText.text.toString()
            if (checkUser(nombreUsuario, contrasena)) {
                Toast.makeText(this, "Login Exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show()
            }
        }

        val btn = findViewById<TextView>(R.id.signupText)
        btn.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegisterActivity::class.java
                )
            )
        }
    }

    private fun checkUser(nombreUsuario: String, contrasena: String): Boolean {
        val db = admin.writableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMNA_CONTRASENA FROM $TABLA_USUARIOS WHERE $COLUMNA_NOMBRE_USUARIO = ?",
            arrayOf(nombreUsuario)
        )
        val almacenada = if (cursor.moveToFirst()) cursor.getString(0) else null
        cursor.close()

        val valida = when {
            almacenada == null -> false
            PasswordHasher.isHashed(almacenada) -> PasswordHasher.verify(contrasena, almacenada)
            // Cuentas antiguas con la contraseña en texto plano: se validan una vez
            // y se migran al formato hasheado.
            almacenada == contrasena -> {
                val valores = ContentValues().apply {
                    put(COLUMNA_CONTRASENA, PasswordHasher.hash(contrasena))
                }
                db.update(TABLA_USUARIOS, valores, "$COLUMNA_NOMBRE_USUARIO = ?", arrayOf(nombreUsuario))
                true
            }
            else -> false
        }
        db.close()
        return valida
    }
}

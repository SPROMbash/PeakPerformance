package com.example.peakperformance

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var administrador: AdminSQLiteOpenHelper
    private lateinit var campoNombreUsuario: EditText
    private lateinit var campoContrasena: EditText
    private lateinit var campoConfirmarContrasena: EditText
    private lateinit var campoEmail: EditText
    private lateinit var botonRegistrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        campoNombreUsuario = findViewById(R.id.inputUsername)
        campoContrasena = findViewById(R.id.inputPassword)
        campoConfirmarContrasena = findViewById(R.id.inputConformPassword)
        campoEmail = findViewById(R.id.inputEmail)
        botonRegistrar = findViewById(R.id.btnRegister)

        administrador = AdminSQLiteOpenHelper(this)

        botonRegistrar.setOnClickListener {
            val nombreUsuario = campoNombreUsuario.text.toString().trim()
            val contrasena = campoContrasena.text.toString().trim()
            val confirmarContrasena = campoConfirmarContrasena.text.toString().trim()
            val email = campoEmail.text.toString().trim()

            if (nombreUsuario.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (contrasena != confirmarContrasena) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!administrador.esEmailValido(email)) {
                Toast.makeText(this, "El email no tiene un formato válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!administrador.esContrasenaValida(contrasena)) {
                Toast.makeText(this, "La contraseña no cumple con los requisitos de seguridad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (registrarUsuario(nombreUsuario, contrasena, email)) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show()
            }
        }

        val btn = findViewById<TextView>(R.id.alreadyHaveAccount)
        btn.setOnClickListener {
            startActivity(
                Intent(
                    this@RegisterActivity,
                    LoginActivity::class.java
                )
            )
        }
    }

    fun registrarUsuario(nombreUsuario: String, contrasena: String, email: String): Boolean {
        val db = administrador.writableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLA_USUARIOS WHERE $COLUMNA_NOMBRE_USUARIO = ?", arrayOf(nombreUsuario))
        if (cursor.count > 0) {
            cursor.close()
            db.close()
            return false
        }
        cursor.close()

        val valores = ContentValues().apply {
            put(COLUMNA_NOMBRE_USUARIO, nombreUsuario)
            put(COLUMNA_CONTRASENA, contrasena)
            put(COLUMNA_EMAIL, email)
        }

        val resultado = db.insert(TABLA_USUARIOS, null, valores)
        db.close()

        return resultado != -1L
    }
}

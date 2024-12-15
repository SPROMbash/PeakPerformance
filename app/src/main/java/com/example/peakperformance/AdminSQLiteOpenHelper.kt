package com.example.peakperformance

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Constantes para la tabla de usuarios
const val TABLA_USUARIOS = "usuarios"
const val COLUMNA_ID = "id"
const val COLUMNA_NOMBRE_USUARIO = "username"
const val COLUMNA_CONTRASENA = "password"
const val COLUMNA_EMAIL = "email"

// Constantes para la tabla de rutinas
const val TABLA_RUTINAS = "rutinas"
const val COLUMNA_RUTINA_ID = "id"
const val COLUMNA_NOMBRE = "nombre"
const val COLUMNA_DESCRIPCION = "descripcion"

// Constantes para la nueva tabla de ejercicios en las rutinas
const val TABLA_RUTINAS_EJERCICIOS = "rutinas_ejercicios"
const val COLUMNA_RUTINA_EJERCICIO_ID = "id"
const val COLUMNA_RUTINA_ID_FK = "rutina_id"
const val COLUMNA_EJERCICIO_ID = "ejercicio_id"
const val COLUMNA_SERIES = "series"
const val COLUMNA_REPETICIONES = "repeticiones"

class AdminSQLiteOpenHelper(context: Context) : SQLiteOpenHelper(
    context,
    "usuarios_db",
    null,
    4 // Versión de la base de datos incrementada
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLA_USUARIOS(" +
                    "$COLUMNA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMNA_NOMBRE_USUARIO TEXT UNIQUE, " +
                    "$COLUMNA_CONTRASENA TEXT, " +
                    "$COLUMNA_EMAIL TEXT)"
        )

        db.execSQL(
            "CREATE TABLE $TABLA_RUTINAS(" +
                    "$COLUMNA_RUTINA_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMNA_NOMBRE TEXT, " +
                    "$COLUMNA_DESCRIPCION TEXT)"
        )

        db.execSQL(
            "CREATE TABLE $TABLA_RUTINAS_EJERCICIOS(" +
                    "$COLUMNA_RUTINA_EJERCICIO_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMNA_RUTINA_ID_FK INTEGER, " +
                    "$COLUMNA_EJERCICIO_ID INTEGER, " +
                    "$COLUMNA_SERIES INTEGER, " +
                    "$COLUMNA_REPETICIONES INTEGER, " +
                    "FOREIGN KEY($COLUMNA_RUTINA_ID_FK) REFERENCES $TABLA_RUTINAS($COLUMNA_RUTINA_ID), " +
                    "FOREIGN KEY($COLUMNA_EJERCICIO_ID) REFERENCES ejercicios(id))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            db.execSQL(
                "CREATE TABLE $TABLA_RUTINAS_EJERCICIOS(" +
                        "$COLUMNA_RUTINA_EJERCICIO_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMNA_RUTINA_ID_FK INTEGER, " +
                        "$COLUMNA_EJERCICIO_ID INTEGER, " +
                        "$COLUMNA_SERIES INTEGER, " +
                        "$COLUMNA_REPETICIONES INTEGER, " +
                        "FOREIGN KEY($COLUMNA_RUTINA_ID_FK) REFERENCES $TABLA_RUTINAS($COLUMNA_RUTINA_ID), " +
                        "FOREIGN KEY($COLUMNA_EJERCICIO_ID) REFERENCES ejercicios(id))"
            )
        }
    }

    fun esContrasenaValida(contrasena: String): Boolean {
        return contrasena.length >= 8 &&
                contrasena.any { it.isDigit() } &&
                contrasena.any { it.isLetter() } &&
                contrasena.any { it.isUpperCase() }
    }

    fun esEmailValido(email: String): Boolean {
        val regexEmail = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$".toRegex()
        return regexEmail.matches(email)
    }

    fun eliminarRutina(idRutina: Int): Boolean {
        val db = writableDatabase
        val result = db.delete(
            TABLA_RUTINAS,
            "$COLUMNA_RUTINA_ID = ?",
            arrayOf(idRutina.toString())
        )
        db.close()
        return result > 0
    }
}

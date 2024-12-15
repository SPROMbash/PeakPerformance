package com.example.peakperformance

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CrearRutinaActivity : AppCompatActivity() {

    private lateinit var dbHelper: AdminSQLiteOpenHelper
    private lateinit var rutinaNombreEditText: EditText
    private lateinit var rutinaDescripcionEditText: EditText
    private lateinit var guardarRutinaButton: Button
    private lateinit var categoriasContainer: LinearLayout

    private val ejerciciosSeleccionados = mutableListOf<EjercicioConDetalles>()

    private val categorias = mapOf(
        "Brazos" to listOf(
            Ejercicio(1, "Curl de Bíceps", "Ejercicio para bíceps"),
            Ejercicio(2, "Extensiones de Tríceps", "Ejercicio para tríceps")
        ),
        "Piernas" to listOf(
            Ejercicio(3, "Sentadillas", "Ejercicio para piernas"),
            Ejercicio(4, "Prensa de Piernas", "Ejercicio para cuádriceps")
        ),
        "Pectoral" to listOf(
            Ejercicio(5, "Press de Banca", "Ejercicio para pectorales"),
            Ejercicio(6, "Aperturas con mancuernas", "Ejercicio para pecho")
        ),
        "Espalda" to listOf(
            Ejercicio(7, "Remo con barra", "Ejercicio para espalda"),
            Ejercicio(8, "Pull-up", "Ejercicio para espalda")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_rutina)

        dbHelper = AdminSQLiteOpenHelper(this)
        rutinaNombreEditText = findViewById(R.id.rutinaNombreEditText)
        rutinaDescripcionEditText = findViewById(R.id.rutinaDescripcionEditText)
        guardarRutinaButton = findViewById(R.id.guardarRutinaButton)
        categoriasContainer = findViewById(R.id.categoriasContainer)

        mostrarEjercicios()

        guardarRutinaButton.setOnClickListener {
            val nombre = rutinaNombreEditText.text.toString()
            val descripcion = rutinaDescripcionEditText.text.toString()

            if (nombre.isNotBlank() && descripcion.isNotBlank() && ejerciciosSeleccionados.isNotEmpty()) {
                guardarRutinaEnBD(nombre, descripcion, ejerciciosSeleccionados)
                Toast.makeText(this, "Rutina guardada con éxito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos y selecciona ejercicios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarEjercicios() {
        for ((categoria, listaEjercicios) in categorias) {
            val categoriaTextView = TextView(this).apply {
                text = categoria
                textSize = 20f
            }
            categoriasContainer.addView(categoriaTextView)

            listaEjercicios.forEach { ejercicio ->
                val ejercicioButton = Button(this).apply {
                    text = ejercicio.nombre
                    setOnClickListener {
                        mostrarDialogoSeriesRepeticiones(ejercicio)
                    }
                }
                categoriasContainer.addView(ejercicioButton)
            }
        }
    }

    private fun mostrarDialogoSeriesRepeticiones(ejercicio: Ejercicio) {
        val dialog = DialogSeriesRepeticiones(this, ejercicio) { series, repeticiones ->
            val ejercicioConDetalles = EjercicioConDetalles(ejercicio, series, repeticiones)
            ejerciciosSeleccionados.add(ejercicioConDetalles)
            Toast.makeText(this, "Ejercicio añadido: ${ejercicio.nombre}", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    private fun guardarRutinaEnBD(nombre: String, descripcion: String, ejerciciosSeleccionados: List<EjercicioConDetalles>) {
        val db: SQLiteDatabase = dbHelper.writableDatabase

        val valoresRutina = ContentValues().apply {
            put(COLUMNA_NOMBRE, nombre)
            put(COLUMNA_DESCRIPCION, descripcion)
        }

        val idRutina = db.insert(TABLA_RUTINAS, null, valoresRutina)

        if (idRutina != -1L) {
            for (ejercicio in ejerciciosSeleccionados) {
                val valoresEjercicio = ContentValues().apply {
                    put(COLUMNA_RUTINA_ID_FK, idRutina)
                    put(COLUMNA_EJERCICIO_ID, ejercicio.ejercicio.id)
                    put(COLUMNA_SERIES, ejercicio.series)
                    put(COLUMNA_REPETICIONES, ejercicio.repeticiones)
                }
                db.insert(TABLA_RUTINAS_EJERCICIOS, null, valoresEjercicio)
            }
        }
    }
}

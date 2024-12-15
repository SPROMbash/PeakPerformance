package com.example.peakperformance

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class RutinasActivity : AppCompatActivity() {

    private lateinit var dbHelper: AdminSQLiteOpenHelper
    private lateinit var containerRutinas: LinearLayout
    private lateinit var crearRutinaButton: Button
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rutinas)

        dbHelper = AdminSQLiteOpenHelper(this)
        containerRutinas = findViewById(R.id.containerRutinas)
        crearRutinaButton = findViewById(R.id.crearRutinaButton)

        // Inicializar el GestureDetector
        gestureDetector = GestureDetector(this, SwipeGestureListener())

        val rutinas = cargarRutinasDesdeBD()
        mostrarRutinas(rutinas)

        crearRutinaButton.setOnClickListener {
            val intent = Intent(this, CrearRutinaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun cargarRutinasDesdeBD(): List<Rutina> {
        val rutinas = mutableListOf<Rutina>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            TABLA_RUTINAS,
            arrayOf(COLUMNA_NOMBRE, COLUMNA_DESCRIPCION),
            null, null, null, null, null
        )

        try {
            if (cursor.moveToFirst()) {
                do {
                    val nombreIndex = cursor.getColumnIndex(COLUMNA_NOMBRE)
                    val descripcionIndex = cursor.getColumnIndex(COLUMNA_DESCRIPCION)

                    if (nombreIndex != -1 && descripcionIndex != -1) {
                        val nombre = cursor.getString(nombreIndex)
                        val descripcion = cursor.getString(descripcionIndex)
                        rutinas.add(Rutina(nombre, descripcion))
                    } else {
                        Log.e("Database Error", "Las columnas no existen en la base de datos.")
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("Database Error", "Error al acceder a la base de datos: ${e.message}")
        } finally {
            cursor.close()
        }

        return rutinas
    }

    private fun mostrarRutinas(rutinas: List<Rutina>) {
        for (rutina in rutinas) {
            val cardView = layoutInflater.inflate(R.layout.item_rutina, containerRutinas, false)

            val routineName = cardView.findViewById<TextView>(R.id.routine_name)
            val routineDescription = cardView.findViewById<TextView>(R.id.routine_description)

            routineName.text = rutina.nombre
            routineDescription.text = rutina.descripcion

            cardView.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
                true
            }

            containerRutinas.addView(cardView)
        }
    }

    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            if (e1 == null) {
                return false
            }

            val SWIPE_THRESHOLD = 100
            val SWIPE_VELOCITY_THRESHOLD = 100

            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y


            if (Math.abs(diffX) > Math.abs(diffY)) {

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {

                    if (diffX < 0) {
                        eliminarTarjeta()
                    }
                    return true
                }
            }
            return false
        }
    }

    private fun eliminarTarjeta() {
        if (containerRutinas.childCount > 0) {
            containerRutinas.removeViewAt(0)
            Toast.makeText(this, "Tarjeta eliminada", Toast.LENGTH_SHORT).show()
        }
    }
}

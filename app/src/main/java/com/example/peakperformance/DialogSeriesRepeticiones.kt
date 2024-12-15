package com.example.peakperformance

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DialogSeriesRepeticiones(
    context: Context,
    private val ejercicio: Ejercicio,
    private val onDatosIngresados: (series: Int, repeticiones: Int) -> Unit
) : Dialog(context) {

    private lateinit var seriesEditText: EditText
    private lateinit var repeticionesEditText: EditText
    private lateinit var guardarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_series_repeticiones)

        seriesEditText = findViewById(R.id.seriesEditText)
        repeticionesEditText = findViewById(R.id.repeticionesEditText)
        guardarButton = findViewById(R.id.guardarButton)

        guardarButton.setOnClickListener {
            val series = seriesEditText.text.toString().toIntOrNull()
            val repeticiones = repeticionesEditText.text.toString().toIntOrNull()

            if (series != null && repeticiones != null) {
                onDatosIngresados(series, repeticiones)
                dismiss()
            } else {
                Toast.makeText(context, "Por favor, ingresa valores válidos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

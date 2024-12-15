package com.example.peakperformance

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat


class SettingsActivity : AppCompatActivity() {

    private lateinit var imagenPerfil: ImageView
    private lateinit var modoOscuroSwitch: SwitchCompat
    private lateinit var notificacionesSwitch: SwitchCompat
    private lateinit var preferenciasCompartidas: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var idiomaSpinner: Spinner
    private lateinit var sincronizacionSwitch: SwitchCompat
    private lateinit var botonTono: Button

    companion object {
        private const val SELECCIONAR_IMAGEN_REQUEST = 1
        private const val SELECCIONAR_TONO_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        preferenciasCompartidas = getSharedPreferences("AJUSTES_USUARIO", MODE_PRIVATE)
        editor = preferenciasCompartidas.edit()

        imagenPerfil = findViewById(R.id.imagen_perfil)
        modoOscuroSwitch = findViewById(R.id.switch_modo_oscuro)
        notificacionesSwitch = findViewById(R.id.switch_notificaciones)
        idiomaSpinner = findViewById(R.id.spinner_idioma)
        sincronizacionSwitch = findViewById(R.id.switch_sincronizacion)
        botonTono = findViewById(R.id.boton_seleccionar_tono)

        val botonCambiarImagen: Button = findViewById(R.id.boton_cambiar_imagen)
        val idiomas = resources.getStringArray(R.array.idiomas)
        val adaptadorIdioma = ArrayAdapter(this, android.R.layout.simple_spinner_item, idiomas)
        idiomaSpinner.adapter = adaptadorIdioma

        sincronizacionSwitch.isChecked = preferenciasCompartidas.getBoolean("sincronizacion_activada", true)
        sincronizacionSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("sincronizacion_activada", isChecked)
            editor.apply()
        }

        botonTono.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
            startActivityForResult(intent, SELECCIONAR_TONO_REQUEST)
        }

        cargarPreferencias()

        botonCambiarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, SELECCIONAR_IMAGEN_REQUEST)
        }

        modoOscuroSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editor.putBoolean("modo_oscuro", true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editor.putBoolean("modo_oscuro", false)
            }
            editor.apply()
        }

        notificacionesSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notificaciones_activadas", isChecked)
            editor.apply()
        }
    }

    private fun cargarPreferencias() {
        val esModoOscuro = preferenciasCompartidas.getBoolean("modo_oscuro", false)
        val sonNotificacionesActivadas = preferenciasCompartidas.getBoolean("notificaciones_activadas", true)

        modoOscuroSwitch.isChecked = esModoOscuro
        notificacionesSwitch.isChecked = sonNotificacionesActivadas
    }

    @Deprecated(
        "onActivityResult is deprecated. Consider using ActivityResultContracts.",
        ReplaceWith("registerForActivityResult()")
    )
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECCIONAR_TONO_REQUEST && resultCode == RESULT_OK && data != null) {
            val uri: Uri? = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            uri?.let {
                editor.putString("uri_tono", it.toString())
                editor.apply()
            }
        }

        if (requestCode == SELECCIONAR_IMAGEN_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imagenUri = data.data
            imagenPerfil.setImageURI(imagenUri)

            editor.putString("uri_imagen_perfil", imagenUri.toString())
            editor.apply()
        }
    }
}

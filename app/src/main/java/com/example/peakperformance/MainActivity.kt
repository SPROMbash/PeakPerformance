package com.example.peakperformance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.speech.RecognizerIntent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var layoutDrawer: DrawerLayout
    private lateinit var interruptorModo: SwitchCompat
    private lateinit var preferenciasCompartidas: SharedPreferences
    private lateinit var editorPreferencias: SharedPreferences.Editor
    private lateinit var textComandoReconocido: TextView
    private lateinit var btnReconocimientoVoz: ImageButton
    private var modoOscuro: Boolean = false

    companion object {
        private const val VOICE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layoutDrawer = findViewById(R.id.drawer_layout)
        val vistaNavegacion: NavigationView = findViewById(R.id.nav_view)
        vistaNavegacion.setNavigationItemSelectedListener(this)

        val barraHerramientas: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(barraHerramientas)

        val toggle = ActionBarDrawerToggle(
            this, layoutDrawer, barraHerramientas, R.string.open_nav, R.string.close_nav
        )
        layoutDrawer.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            vistaNavegacion.setCheckedItem(R.id.nav_home)
        }

        val tarjetaRutinas: CardView = findViewById(R.id.rutinasCard)
        val tarjetaProgreso: CardView = findViewById(R.id.progresoCard)
        val tarjetaNutricion: CardView = findViewById(R.id.nutricionCard)
        val tarjetaSalud: CardView = findViewById(R.id.saludCard)
        val tarjetaClases: CardView = findViewById(R.id.clasesCard)
        val tarjetaLogros: CardView = findViewById(R.id.logrosCard)

        interruptorModo = findViewById(R.id.switchMode)
        btnReconocimientoVoz = findViewById(R.id.btnReconocimientoVoz)
        textComandoReconocido = findViewById(R.id.txtComandoReconocido)

        setCardViewClickEffect(tarjetaRutinas)
        setCardViewClickEffect(tarjetaProgreso)
        setCardViewClickEffect(tarjetaNutricion)
        setCardViewClickEffect(tarjetaSalud)
        setCardViewClickEffect(tarjetaClases)
        setCardViewClickEffect(tarjetaLogros)

        tarjetaRutinas.setOnClickListener {
            val intent = Intent(this, RutinasActivity::class.java)
            startActivity(intent)
        }

        tarjetaProgreso.setOnClickListener {
            val intent = Intent(this, ProgresoActivity::class.java)
            startActivity(intent)
        }

        tarjetaNutricion.setOnClickListener {
            val intent = Intent(this, NutricionActivity::class.java)
            startActivity(intent)
        }

        tarjetaSalud.setOnClickListener {
            val intent = Intent(this, SaludActivity::class.java)
            startActivity(intent)
        }

        tarjetaClases.setOnClickListener {
            val intent = Intent(this, ClasesActivity::class.java)
            startActivity(intent)
        }

        tarjetaLogros.setOnClickListener {
            val intent = Intent(this, LogrosActivity::class.java)
            startActivity(intent)
        }

        preferenciasCompartidas = getSharedPreferences("MODE", Context.MODE_PRIVATE)
        modoOscuro = preferenciasCompartidas.getBoolean("nightMode", false)

        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            cambiarColoresAModoOscuro()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            cambiarColoresAModoClaro()
        }

        interruptorModo.isChecked = modoOscuro

        interruptorModo.setOnClickListener {
            cambiarModoOscuro()
        }

        btnReconocimientoVoz.setOnClickListener {
            iniciarReconocimientoDeVoz()
        }
    }

    private fun iniciarReconocimientoDeVoz() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di un comando, por ejemplo: 'Iniciar rutinas' o 'Modo oscuro'")

        try {
            startActivityForResult(intent, VOICE_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "El reconocimiento de voz no está disponible en este dispositivo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == VOICE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val comando = resultado?.get(0)?.lowercase(Locale.getDefault()) ?: ""

            textComandoReconocido.text = comando

            when {
                comando.contains("iniciar rutinas") -> {
                    val intent = Intent(this, RutinasActivity::class.java)
                    startActivity(intent)
                }

                comando.contains("modo oscuro") -> {
                    cambiarModoOscuro()
                }

                comando.contains("notificaciones") -> {
                    Toast.makeText(this, "Función de notificaciones no implementada", Toast.LENGTH_SHORT).show()
                }

                else -> {
                    Toast.makeText(this, "Comando no reconocido: $comando", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun cambiarModoOscuro() {
        modoOscuro = !modoOscuro
        if (modoOscuro) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            cambiarColoresAModoOscuro()
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            cambiarColoresAModoClaro()
        }
        editorPreferencias = preferenciasCompartidas.edit()
        editorPreferencias.putBoolean("nightMode", modoOscuro)
        editorPreferencias.apply()
    }

    private fun cambiarColoresAModoOscuro() {
        val vistaNavegacion: NavigationView = findViewById(R.id.nav_view)
        vistaNavegacion.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.white))
        vistaNavegacion.setItemTextColor(ContextCompat.getColorStateList(this, R.color.white))

        val barraHerramientas: Toolbar = findViewById(R.id.toolbar)
        barraHerramientas.setBackgroundColor(ContextCompat.getColor(this, R.color.darkGrey))
        barraHerramientas.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        val btnReconocimientoVoz: ImageButton = findViewById(R.id.btnReconocimientoVoz)

        btnReconocimientoVoz.setColorFilter(ContextCompat.getColor(this, R.color.white))
    }

    private fun cambiarColoresAModoClaro() {
        val vistaNavegacion: NavigationView = findViewById(R.id.nav_view)
        vistaNavegacion.setItemIconTintList(ContextCompat.getColorStateList(this, R.color.lavender))
        vistaNavegacion.setItemTextColor(ContextCompat.getColorStateList(this, R.color.lavender))

        val barraHerramientas: Toolbar = findViewById(R.id.toolbar)
        barraHerramientas.setBackgroundColor(ContextCompat.getColor(this, R.color.lavender))
        barraHerramientas.setTitleTextColor(ContextCompat.getColor(this, R.color.white))

        val btnReconocimientoVoz: ImageButton = findViewById(R.id.btnReconocimientoVoz)
        btnReconocimientoVoz.setColorFilter(ContextCompat.getColor(this, R.color.lavender))  // Cambiar el color del icono
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCardViewClickEffect(cardView: CardView) {
        cardView.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    cardView.cardElevation = 10f
                    cardView.scaleX = 0.97f
                    cardView.scaleY = 0.97f
                }
                MotionEvent.ACTION_UP -> {
                    cardView.cardElevation = 20f
                    cardView.scaleX = 1f
                    cardView.scaleY = 1f
                    view.performClick()
                }
                MotionEvent.ACTION_CANCEL -> {
                    cardView.cardElevation = 20f
                    cardView.scaleX = 1f
                    cardView.scaleY = 1f
                }
            }
            true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .commit()
            }
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_share -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ShareFragment())
                    .commit()
            }
            R.id.nav_about -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, AboutFragment())
                    .commit()
            }
            R.id.nav_logout -> {
                Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
            }
        }
        layoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (layoutDrawer.isDrawerOpen(GravityCompat.START)) {
            layoutDrawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
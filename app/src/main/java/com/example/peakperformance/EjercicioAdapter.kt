package com.example.peakperformance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class EjercicioAdapter(
    private val context: Context,
    private val ejercicios: List<Ejercicio>,
    private val onItemClicked: (Ejercicio) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = ejercicios.size

    override fun getItem(position: Int): Any = ejercicios[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_ejercicio, parent, false)

        val ejercicio = ejercicios[position]
        val nombreTextView: TextView = view.findViewById(R.id.ejercicioNombre)

        nombreTextView.text = ejercicio.nombre

        view.setOnClickListener { onItemClicked(ejercicio) }

        return view
    }
}

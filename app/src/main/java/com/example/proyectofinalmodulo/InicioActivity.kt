package com.example.proyectofinalmodulo

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Adapter.MascotaAdapter
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.databinding.ActivityAnadirMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.example.proyectofinalmodulo.databinding.ListadoBinding
import com.google.firebase.firestore.FirebaseFirestore

class InicioActivity : MenuActivity() {

    lateinit var binding: ActivityInicioBinding
    lateinit var binding3: ListadoBinding
    private lateinit var mascotasRecyclerView: RecyclerView
    private lateinit var mascotasArrayList: ArrayList<MascotasData>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mascotasArrayList = ArrayList()
        mascotasRecyclerView = RecyclerView(applicationContext)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        binding3 = ListadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)

        binding.mascotasLista.layoutManager = LinearLayoutManager(this)
        binding.mascotasLista.adapter = MascotaAdapter(mascotasArrayList)
        binding.mascotasLista.addItemDecoration(decoration)

        cargarDatos()

        actividadActual = 0

    }






    fun cargarDatos() {


        val db = FirebaseFirestore.getInstance()
        // Obtengo los datos de la base de datos
        db.collection("Mascotas")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Añadiendo Mascotas", "${document.id} => ${document.data}")
                    val mascota = document.toObject(MascotasData::class.java)
                    mascotasArrayList.add(mascota)
                    // Muestro el recyclerView
                    binding.mascotasLista.layoutManager = LinearLayoutManager(this)
                    binding.mascotasLista.adapter = MascotaAdapter(mascotasArrayList)
                    // Vuelvo a actualizar el adapter para el borrado
                    var adapter = MascotaAdapter(mascotasArrayList)
                    mascotasRecyclerView?.adapter = adapter
                    val position = mascotasArrayList.indexOf(MascotasData())
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()


                }


            }
            .addOnFailureListener { exception ->
                Log.w("Añadiendo Mascotas", "Error al obtener las mascotas.", exception)
            }


    }




}
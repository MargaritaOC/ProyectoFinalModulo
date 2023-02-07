package com.example.proyectofinalmodulo

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalmodulo.Adapter.MascotaAdapter
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.http.Url

class InicioActivity : MenuActivity() {

    lateinit var binding: ActivityInicioBinding
    private lateinit var  mascotasRecyclerView: RecyclerView
    private lateinit var mascotasArrayList: ArrayList<MascotasData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mascotasArrayList = ArrayList()
        mascotasRecyclerView = RecyclerView(applicationContext)
        binding = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)

        binding.mascotasLista.layoutManager = LinearLayoutManager(this)
        binding.mascotasLista.adapter=MascotaAdapter(mascotasArrayList)
        binding.mascotasLista.addItemDecoration(decoration)


        cargarDatos()

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
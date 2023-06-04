package com.example.proyectofinalmodulo

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Adapter.MascotaAdapter
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.databinding.ActivityBuscarMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInfoMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class InfoMascotaActivity : MenuActivity() {

    private lateinit var binding: ActivityInfoMascotaBinding
    private lateinit var binding2: ActivityInicioBinding
    private lateinit var adapter: MascotaAdapter
    private lateinit var mascotasRecyclerView: RecyclerView
    private lateinit var mascotasArrayList: ArrayList<MascotasData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoMascotaBinding.inflate(layoutInflater)
        binding2 = ActivityInicioBinding.inflate(layoutInflater)
        mascotasArrayList = ArrayList()
        adapter = MascotaAdapter(mascotasArrayList)
        mascotasRecyclerView = RecyclerView(applicationContext)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()

        val id = intent.getStringExtra("idM")?.toIntOrNull()

        if (id != null) {
            db.collection("Mascotas")
                .whereEqualTo("idM", id)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val mascota = querySnapshot.documents.first()
                        binding.PTidM.text = "Id: " + mascota.getLong("idM").toString()
                        binding.PTnombreM.text = "Nombre: ${mascota.getString("nombre")}"
                        binding.PTestadoM.text = "Estado: ${mascota.getString("Estado")}"
                        binding.PTdescripcionM.text =
                            "Descripcion: ${mascota.getString("descripcion")}"
                        binding.PTedadM.text = "Edad: ${mascota.getString("edad")}"
                        binding.PTlocalidadM.text = "Localidad: ${mascota.getString("localidad")}"
                        binding.PTrazaM.text = "Raza: ${mascota.getString("raza")}"
                        binding.PTsexoM.text = "Sexo: ${mascota.getString("sexo")}"
                        binding.PTespecieM.text = "Especie: ${mascota.getString("especie")}"
                        binding.PTvacunadoM.text = "Vacunado: ${mascota.getString("vacunado")}"
                        binding.PTesterilizadoM.text =
                            "Esterilizado: ${mascota.getString("esterilizado")}"
                        binding.PTduenoM.text = "Dueño: ${mascota.getString("dueño")}"
                        binding.PTtelefonoM.text = "Telefono: ${mascota.getString("telefono")}"
                        binding.PTemailM.text = "Email: ${mascota.getString("gmail")}"

                        val urlImagen = mascota.getString("imagen")

                        Glide.with(this)
                            .load(urlImagen)
                            .into(binding.imagenMascota)

                        var isImageEnlarged = false
                        val originalWidth = 1000
                        val originalHeight = 420
                        val enlargedWidth = 1000
                        val enlargedHeight = 1000

                        binding.imagenMascota.setOnClickListener {
                            val layoutParams = binding.imagenMascota.layoutParams
                            if (!isImageEnlarged) {
                                // Si la imagen no está agrandada, cambiar al tamaño más grande
                                layoutParams.width = enlargedWidth
                                layoutParams.height = enlargedHeight
                                isImageEnlarged = true
                            } else {
                                // Si la imagen está agrandada, restaurar el tamaño original
                                layoutParams.width = originalWidth
                                layoutParams.height = originalHeight
                                isImageEnlarged = false
                            }
                            binding.imagenMascota.requestLayout()
                        }


                        binding.botonAdoptar.setOnClickListener {

                            if (mascota.getString("Estado") == "Adoptado") {
                                binding.botonAdoptar.isEnabled = false
                                Toast.makeText(
                                    this,
                                    "Esta mascota ya esta adoptada",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            } else {

                                val mascotaRef = mascota.reference
                                // Lógica adicional para realizar la adopción de la mascota
                                mascotaRef.update("Estado", "Adoptado")
                                    .addOnSuccessListener {
                                        binding.botonAdoptar.isEnabled = false
                                        Toast.makeText(
                                            this,
                                            "Se adopto correctamente",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()

                                        cargarDatos()

                                        val intent = Intent(this, InicioActivity::class.java)
                                        actividadActual = 0
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        // Manejo de errores en caso de falla en la actualización de datos
                                        Toast.makeText(
                                            this,
                                            "Error al querer adoptar",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                            }
                        }


                    } else {
                        Toast.makeText(
                            this,
                            "ID de mascota no válido",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
    }



    private fun cargarDatos() {

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
                    binding2.mascotasLista.layoutManager = LinearLayoutManager(this)
                    binding2.mascotasLista.adapter = MascotaAdapter(mascotasArrayList)
                    // Vuelvo a actualizar el adapter para el borrado
                    var adapter = MascotaAdapter(mascotasArrayList)
                    mascotasRecyclerView?.adapter = adapter
                    val position = mascotasArrayList.indexOf(MascotasData())
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()


                }


            }

        }
    }


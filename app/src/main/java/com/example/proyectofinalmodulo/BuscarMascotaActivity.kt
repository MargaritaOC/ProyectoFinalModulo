package com.example.proyectofinalmodulo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.databinding.ActivityBuscarMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInfoMascotaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BuscarMascotaActivity : MenuActivity() {
    lateinit var binding: ActivityBuscarMascotaBinding
    lateinit var binding2: ActivityInfoMascotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuscarMascotaBinding.inflate(layoutInflater)
        binding2 = ActivityInfoMascotaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = FirebaseFirestore.getInstance()
        actividadActual = 4

        binding.bBuscarMascota.setOnClickListener {
            if (binding.infoId.text.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, ingrese un valor para el campo Chip",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val idMascota = binding.infoId.text.toString().toInt()

            db.collection("Mascotas")
                .whereEqualTo("idM", idMascota)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val intent = Intent(this, InfoMascotaActivity::class.java)
                        intent.putExtra("idM", idMascota.toString())
                        startActivity(intent)

                        binding2.PTidM.text = "Id: $idMascota"
                        binding.infoId.text.clear()
                        // Agrega aquí el código para mostrar los demás datos de la mascota en los campos correspondientes
                    } else {
                        Toast.makeText(
                            this,
                            "No se encontró ninguna mascota con ese ID",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
        }

        actividadActual = 2
    }
}
package com.example.proyectofinalmodulo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.databinding.ActivityBuscarMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInfoMascotaBinding
import com.google.firebase.firestore.FirebaseFirestore

class InfoMascotaActivity : MenuActivity() {
    private lateinit var binding: ActivityInfoMascotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoMascotaBinding.inflate(layoutInflater)
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
                        binding.PTdescripcionM.text = "Descripcion: ${mascota.getString("descripcion")}"
                        binding.PTedadM.text = "Edad: ${mascota.getString("edad")}"
                        binding.PTlocalidadM.text = "Localidad: ${mascota.getString("localidad")}"
                        binding.PTrazaM.text = "Raza: ${mascota.getString("raza")}"
                        binding.PTsexoM.text = "Sexo: ${mascota.getString("sexo")}"
                        binding.PTespecieM.text = "Especie: ${mascota.getString("especie")}"
                        binding.PTvacunadoM.text = "Vacunado: ${mascota.getString("vacunado")}"
                        binding.PTesterilizadoM.text = "Esterilizado: ${mascota.getString("esterilizado")}"
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

                    } else {
                        Toast.makeText(
                            this,
                            "No se encontró ninguna mascota con ese ID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(
                this,
                "ID de mascota no válido",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}
package com.example.proyectofinalmodulo.Adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.databinding.ListadoBinding



class MascotaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ListadoBinding.bind(view)
    fun render(mascotaModel: MascotasData) {

        binding.nombreMascota.text = "Nombre: ${mascotaModel.nombre}"
        binding.anioMascota.text = "Edad: ${mascotaModel.edad}"
        binding.estadoMascota.text = mascotaModel.Estado
        binding.especieMascota.text = "Especie: ${mascotaModel.especie}"
        Log.d("MascotaAdapter", "idM: ${mascotaModel.idM}")
        binding.idMascota.text = "Id: " + mascotaModel.idM



        Glide.with(binding.fotoMascota.context).load(mascotaModel.imagen).into(binding.fotoMascota)

        var isImageEnlarged = false
        val originalWidth = 490
        val originalHeight = 430
        val enlargedWidth = 1000
        val enlargedHeight = 1000

        binding.fotoMascota.setOnClickListener {
            val layoutParams = binding.fotoMascota.layoutParams
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
            binding.fotoMascota.requestLayout()
        }

    }

}





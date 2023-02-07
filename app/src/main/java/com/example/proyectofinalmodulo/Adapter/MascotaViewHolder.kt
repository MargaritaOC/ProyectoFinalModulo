package com.example.proyectofinalmodulo.Adapter

import android.content.ClipData
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.R
import com.example.proyectofinalmodulo.databinding.ActivityAnadirMascotaBinding
import com.example.proyectofinalmodulo.databinding.ListadoBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import io.grpc.Context.Storage



class MascotaViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ListadoBinding.bind(view)


    fun render(mascotaModel: MascotasData) {
        binding.nombreMascota.text = mascotaModel.nombre
        binding.anioMascota.text = mascotaModel.edad
        binding.estadoMascota.text = mascotaModel.Estado
        binding.especieMascota.text = mascotaModel.especie
        Glide.with(binding.fotoMascota.context).load(mascotaModel.imagen).into(binding.fotoMascota)

       binding.fotoMascota.setOnClickListener {
            Toast.makeText(
                binding.fotoMascota.context,
                mascotaModel.nombre,
                Toast.LENGTH_SHORT
            ).show()
        }
        itemView.setOnClickListener {
            Toast.makeText(
                binding.fotoMascota.context,
                mascotaModel.nombre,
                Toast.LENGTH_SHORT
            ).show()
        }

    }
}
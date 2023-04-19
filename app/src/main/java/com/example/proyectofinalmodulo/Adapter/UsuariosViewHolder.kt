package com.example.proyectofinalmodulo.Adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalmodulo.Models.UsuariosData
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding

class UsuariosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ActivityRegistroBinding.bind(view)


    fun render(usuariosModel: UsuariosData) {
        binding.nombreP.text = usuariosModel.nombre,
        binding.apellidosP.text = usuariosModel.apellidos,
        binding.emailP.text = usuariosModel.gmail
    }
}
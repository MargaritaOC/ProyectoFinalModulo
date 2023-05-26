package com.example.proyectofinalmodulo.Adapter

import android.text.Editable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Models.UsuariosData
import com.example.proyectofinalmodulo.databinding.ActivityModificarUsuarioBinding
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding

class UsuariosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ActivityRegistroBinding.bind(view)
    val binding2 = ActivityModificarUsuarioBinding.bind(view)

    fun render(usuariosModel: UsuariosData) {

        val nombreEditable: Editable = Editable.Factory.getInstance().newEditable(usuariosModel.nombre)
        val apellidosEditable: Editable = Editable.Factory.getInstance().newEditable(usuariosModel.apellidos)
        val emailEditable: Editable = Editable.Factory.getInstance().newEditable(usuariosModel.gmail)

        Glide.with(binding2.BIusuario.context).load(usuariosModel.imagen).into(binding2.BIusuario)

        binding.nombreP.text = nombreEditable
        binding.apellidosP.text = apellidosEditable
        binding.emailP.text = emailEditable
    }
}
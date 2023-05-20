package com.example.proyectofinalmodulo.Adapter

import android.text.Editable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalmodulo.Models.UsuariosData
import com.example.proyectofinalmodulo.databinding.ActivityMainBinding
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding

class UsuariosViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val binding = ActivityRegistroBinding.bind(view)

    fun render(usuariosModel: UsuariosData) {

        val nombreEditable: Editable = Editable.Factory.getInstance().newEditable(usuariosModel.nombre)
        val apellidosEditable: Editable = Editable.Factory.getInstance().newEditable(usuariosModel.apellidos)
        val emailEditable: Editable = Editable.Factory.getInstance().newEditable(usuariosModel.gmail)

        binding.nombreP.text = nombreEditable
        binding.apellidosP.text = apellidosEditable
        binding.emailP.text = emailEditable
    }
}
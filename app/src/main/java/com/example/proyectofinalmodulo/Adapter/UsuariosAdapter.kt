package com.example.proyectofinalmodulo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.Models.UsuariosData
import com.example.proyectofinalmodulo.R

class UsuariosAdapter(private val usuariosList: ArrayList<UsuariosData>) : RecyclerView.Adapter<UsuariosViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuariosViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_registro, parent, false)
        return UsuariosViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return usuariosList.size
    }

    override fun onBindViewHolder(holder: UsuariosViewHolder, position: Int) {
        val mostraritem = usuariosList[position]
        holder.render(mostraritem)
    }
}
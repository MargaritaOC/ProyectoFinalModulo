package com.example.proyectofinalmodulo.Adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.R
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream



class MascotaAdapter(private val mascotasList: ArrayList<MascotasData>) : RecyclerView.Adapter<MascotaViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.listado, parent, false)
        return MascotaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MascotaViewHolder, position: Int) {

        val mostraritem = mascotasList[position]
        holder.render(mostraritem)

    }

    override fun getItemCount(): Int {
        return mascotasList.size
    }



}


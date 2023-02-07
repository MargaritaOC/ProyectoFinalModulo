package com.example.proyectofinalmodulo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Adapter.MascotaAdapter
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.camara.CamaraActivity
import com.example.proyectofinalmodulo.databinding.ActivityAnadirMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding
import com.example.proyectofinalmodulo.databinding.ListadoBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.annotations.concurrent.Background
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.grpc.Context.Storage
import retrofit2.http.Url
import java.io.ByteArrayOutputStream

class AnadirMascota : MenuActivity() {
    lateinit var binding: ActivityAnadirMascotaBinding
    lateinit var binding2:ListadoBinding
    lateinit var binding3: ActivityInicioBinding
    lateinit var imagenes: ImageButton
    lateinit var storage : FirebaseStorage
    lateinit var storageUrl: FirebaseStorage
    lateinit var storageRefe: StorageReference
    lateinit var IMGref : StorageReference
    lateinit var boton : Button
    val db = FirebaseFirestore.getInstance()

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
            uri ->
        if(uri!=null){
            imagenes.setImageURI(uri)
            subirIMG()
            conseguirUrl()

        }else{
            Toast.makeText(this, "Error la imagen no existe", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnadirMascotaBinding.inflate(layoutInflater)
        binding2 = ListadoBinding.inflate(layoutInflater)
        binding3 = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagenes = binding.subirImagen

        binding.subirImagen.setOnClickListener{
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))


        }

        binding.BregistrarM.setOnClickListener {
            if (binding.chipM.text.isNotEmpty() && binding.edadM.text.isNotEmpty()
                && binding.nombreM.text.isNotEmpty() && binding.estadoM.text.isNotEmpty()
                &&binding.descripcionM.text.isNotEmpty() && binding.esterilizadoM.text.isNotEmpty()
                && binding.localidadM.text.isNotEmpty() && binding.razaM.text.isNotEmpty()
                &&binding.sexoM.text.isNotEmpty() && binding.tipoM.text.isNotEmpty()
                && binding.vacunadoM.text.isNotEmpty()){


                        db.collection("Mascotas").document(binding.chipM.text.toString())
                            .set(mapOf(
                                "nombre" to binding.nombreM.text.toString(),
                                "Estado" to binding.estadoM.text.toString(),
                                "descripcion" to binding.descripcionM.text.toString(),
                                "edad" to binding.edadM.text.toString(),
                                "localidad" to binding.localidadM.text.toString(),
                                "raza" to binding.razaM.text.toString(),
                                "sexo" to binding.sexoM.text.toString(),
                                "especie" to binding.tipoM.text.toString(),
                                "vacunado" to binding.vacunadoM.text.toString(),
                                "esterilizado" to binding.esterilizadoM.text.toString(),
                                "imagen" to binding.subirImagen.toString(),

                                ))
                Toast.makeText(this, "El registro de la nueva mascota se realizo correctamente", Toast.LENGTH_SHORT).show()

                        //accedemos a la pantalla inicioActivity
                        val intent = Intent(this, InicioActivity::class.java).apply {
                            putExtra("nombremascota", binding.nombreM.text.toString())
                        }
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Error en el registro de la nueva mascota", Toast.LENGTH_SHORT).show()
                    }


            //actualizarDatos()
        }


        binding.BeliminarM.setOnClickListener {
            db.collection("Mascotas")
                .document(binding.chipM.text.toString())
                .delete()

        }

        binding.BactualizarM.setOnClickListener {
            if (binding.chipM.text.isNotEmpty() && binding.edadM.text.isNotEmpty()
                && binding.nombreM.text.isNotEmpty() && binding.estadoM.text.isNotEmpty()
                &&binding.descripcionM.text.isNotEmpty() && binding.esterilizadoM.text.isNotEmpty()
                && binding.localidadM.text.isNotEmpty() && binding.razaM.text.isNotEmpty()
                &&binding.sexoM.text.isNotEmpty() && binding.tipoM.text.isNotEmpty()
                && binding.vacunadoM.text.isNotEmpty()) {


                db.collection("Mascotas").document(binding.chipM.text.toString())
                    .set(
                        mapOf(
                            "nombre" to binding.nombreM.text.toString(),
                            "Estado" to binding.estadoM.text.toString(),
                            "descripcion" to binding.descripcionM.text.toString(),
                            "edad" to binding.edadM.text.toString(),
                            "localidad" to binding.localidadM.text.toString(),
                            "raza" to binding.razaM.text.toString(),
                            "sexo" to binding.sexoM.text.toString(),
                            "especie" to binding.tipoM.text.toString(),
                            "vacunado" to binding.vacunadoM.text.toString(),
                            "esterilizado" to binding.esterilizadoM.text.toString(),
                            "imagen" to binding.subirImagen.toString(),

                            )
                    )
            }
        }

    }

    private fun subirIMG(){
        storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val ruta = storageRef.child("IMGmascotas/" + binding.subirImagen.toString() + ".jpeg")

        val bitmap = (boton.drawableState as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = ruta.putBytes(data)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Mascotas")
                databaseRef.push().setValue(downloadUrl)
            }.addOnSuccessListener  {
                Log.i("Imagen","Imagen subida correctamente")
            }
        }.addOnFailureListener {
            Log.i("Imagen","Error al subir la imagen")
        }
    }

    private fun conseguirUrl(){


        storageUrl = FirebaseStorage.getInstance()
        this.storageRefe = storageUrl.getReference("IMGmascotas/")
        IMGref = storageRefe.child(binding.subirImagen.toString() + ".jpeg")
        IMGref.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0,it.size)
            binding2.fotoMascota.setImageBitmap(bitmap)
            Log.i("Imagen","Imagen cargada correctamente")
        }.addOnFailureListener {
            Log.i("Imagen","Error al cargar la imagen")
        }

    }


   /* fun actualizarDatos() {
        val recyclerView = findViewById<RecyclerView>(R.id.mascotasLista)
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Mascotas").get()
            .addOnSuccessListener { result ->
                val datos = result.toObjects(MascotasData::class.java)
                recyclerView.adapter = MascotaAdapter(datos as ArrayList<MascotasData>)
            }
    }
    */

}
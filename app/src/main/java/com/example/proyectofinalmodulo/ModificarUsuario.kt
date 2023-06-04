package com.example.proyectofinalmodulo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Adapter.UsuariosAdapter
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Models.UsuariosData
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.example.proyectofinalmodulo.databinding.ActivityModificarUsuarioBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ModificarUsuario : MenuActivity() {
    lateinit var binding: ActivityModificarUsuarioBinding
    lateinit var binding2: ActivityInicioBinding
    lateinit var imagenes: ImageView
    lateinit var adapter: UsuariosAdapter
    lateinit var usuariosModel: UsuariosData
    private lateinit var mascotasRecyclerView: RecyclerView
    private lateinit var usuariosArrayList: ArrayList<UsuariosData>
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityModificarUsuarioBinding.inflate(layoutInflater)
        binding2 = ActivityInicioBinding.inflate(layoutInflater)
        imagenes = binding.BIusuario
        usuariosModel = UsuariosData()
        usuariosArrayList = ArrayList()
        mascotasRecyclerView = RecyclerView(applicationContext)
        adapter = UsuariosAdapter(usuariosArrayList)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()
        val userEmail = FirebaseAuth.getInstance().currentUser?.email

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imagenes.setImageURI(uri)


            } else {
                Toast.makeText(this, "Error la imagen no existe", Toast.LENGTH_SHORT).show()
            }
        }

        val pickFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val foto: Bitmap? = data?.extras?.get("data") as Bitmap?
                foto?.let {
                    binding.BIusuario.setImageBitmap(it)
                }
            }
        }


        db.collection("Usuarios").whereEqualTo("gmail", userEmail)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val usuario = querySnapshot.documents.first()
                    val nombre = usuario.getString("nombre")
                    val apellidos = usuario.getString("apellidos")

                    binding.nombreP.text = Editable.Factory.getInstance().newEditable(nombre)
                    binding.apellidosP.text = Editable.Factory.getInstance().newEditable(apellidos)

                    val urlImagen = usuario.getString("imagen")

                    Glide.with(this)
                        .load(urlImagen)
                        .into(binding.BIusuario)
                }
            }

        binding.botongaleria.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        binding.botonfoto.setOnClickListener {
            pickFoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }

        binding.BActualizarP.setOnClickListener {

                // Realiza la consulta en Firestore para encontrar al usuario con el correo electrónico
                val query = db.collection("Usuarios").whereEqualTo("gmail", userEmail)

                query.get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {

                            // Subir la imagen a Firebase Storage
                            subirIMG { imageUri ->
                                // Actualizar la URL de la imagen en Firestore
                                db.collection("Usuarios").document(document.id)
                                    .update(
                                        "nombre", binding.nombreP.text.toString(),
                                        "apellidos", binding.apellidosP.text.toString(),
                                        "imagen", imageUri.toString()
                                    )
                                    .addOnSuccessListener {
                                        // Actualización exitosa
                                        Toast.makeText(this, "Nombre, apellido e imagen actualizados correctamente", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, InicioActivity::class.java)
                                        actividadActual = 0
                                        startActivity(intent)

                                        cargarDatos()
                                    }
                                    .addOnFailureListener { e ->
                                        // Error al actualizar
                                        Toast.makeText(this, "Error al actualizar nombre, apellido e imagen", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Error al realizar la consulta
                        Toast.makeText(this, "Error al obtener el usuario", Toast.LENGTH_SHORT)
                            .show()
                    }
        }

        }


    private fun subirIMG(callback: (Uri) -> Unit) {
        val timestamp = System.currentTimeMillis()
        val fileName = "IMGUsuarios_$timestamp.jpg"
        val storageRef = FirebaseStorage.getInstance().getReference("//IMGUsuarios/$fileName")

        val bitmap = (binding.BIusuario.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = storageRef.putBytes(data)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri) // Llama al callback con la URL de descarga de la imagen
            } else {
                // Error al subir la imagen
                Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarDatos() {

        val db = FirebaseFirestore.getInstance()
        // Obtengo los datos de la base de datos
        db.collection("Usuarios")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("Añadiendo Usuarios", "${document.id} => ${document.data}")
                    val usuario = document.toObject(UsuariosData::class.java)
                    usuariosArrayList.add(usuario)
                    // Muestro el recyclerView
                    binding2.mascotasLista.layoutManager = LinearLayoutManager(this)
                    binding2.mascotasLista.adapter = UsuariosAdapter(usuariosArrayList)
                    // Vuelvo a actualizar el adapter para el borrado
                    var adapter = UsuariosAdapter(usuariosArrayList)
                    mascotasRecyclerView?.adapter = adapter
                    val position = usuariosArrayList.indexOf(UsuariosData())
                    adapter.notifyItemRemoved(position)
                    adapter.notifyDataSetChanged()


                }


            }   }


    }
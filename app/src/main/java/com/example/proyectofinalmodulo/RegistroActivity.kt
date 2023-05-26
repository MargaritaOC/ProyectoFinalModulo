package com.example.proyectofinalmodulo

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Menu.MenuActivity.Companion.actividadActual
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class RegistroActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistroBinding
    lateinit var imagenes: ImageButton
    val db = FirebaseFirestore.getInstance()

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
                binding.imageView.setImageBitmap(it)
            }
        } else {
            startActivity(Intent(this, AnadirMascota::class.java))
        }
    }

    private fun subirIMG(callback: (Uri) -> Unit) {
        val timestamp = System.currentTimeMillis()
        val fileName = "IMGUsuarios_$timestamp.jpg"
        val storageRef = FirebaseStorage.getInstance().getReference("//IMGUsuarios/$fileName")

        val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagenes = binding.imageView

        title = "Nuevo usuario" // Cambia el título de la pantalla
        binding.imageView.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }
        binding.BregistrarP.setOnClickListener {
            if (binding.emailP.text.isNotEmpty() && binding.passwordPRegistro.text.isNotEmpty()
                && binding.nombreP.text.isNotEmpty() && binding.apellidosP.text.isNotEmpty()
            ) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailP.text.toString(), binding.passwordPRegistro.text.toString()
                ).addOnCompleteListener { registrationTask ->
                    if (registrationTask.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser

                        // Enviar correo electrónico de verificación
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { emailVerificationTask ->
                                if (emailVerificationTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registro exitoso. Se ha enviado un correo de verificación.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val usuario = hashMapOf(
                                        "nombre" to binding.nombreP.text.toString(),
                                        "apellidos" to binding.apellidosP.text.toString(),
                                        "gmail" to binding.emailP.text.toString()
                                    )

                                    subirIMG { imageUri ->
                                        // Convertir el objeto Uri a una cadena de texto
                                        val urlImagen = imageUri.toString()

                                        val usuario = hashMapOf(
                                            "nombre" to binding.nombreP.text.toString(),
                                            "apellidos" to binding.apellidosP.text.toString(),
                                            "gmail" to binding.emailP.text.toString(),
                                            "imagen" to urlImagen
                                        )

                                        db.collection("Usuarios")
                                            .document(binding.emailP.text.toString())
                                            .set(usuario)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    this,
                                                    "Usuario registrado correctamente",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                val intent =
                                                    Intent(this, MainActivity::class.java).apply {
                                                        putExtra(
                                                            "nombreusuario",
                                                            binding.nombreP.text.toString()
                                                        )
                                                    }
                                                startActivity(intent)
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    this,
                                                    "Error al registrar el usuario",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Error en el envío del correo de verificación",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            this,
                            "Error en el registro del nuevo usuario",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Algun campo está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
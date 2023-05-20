package com.example.proyectofinalmodulo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegistroBinding
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Nuevo usuario" // Cambia el título de la pantalla
        binding.BregistrarP.setOnClickListener {
            if (binding.emailP.text.isNotEmpty() && binding.passwordPRegistro.text.isNotEmpty()
                && binding.nombreP.text.isNotEmpty() && binding.apellidosP.text.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailP.text.toString(), binding.passwordPRegistro.text.toString()
                ).addOnCompleteListener { registrationTask ->
                    if (registrationTask.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser

                        // Enviar correo electrónico de verificación
                        user?.sendEmailVerification()?.addOnCompleteListener { emailVerificationTask ->
                            if (emailVerificationTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Registro exitoso. Se ha enviado un correo de verificación.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Guardar datos adicionales del usuario en Firestore
                                db.collection("Usuarios").document(binding.emailP.text.toString())
                                    .set(
                                        mapOf(
                                            "nombre" to binding.nombreP.text.toString(),
                                            "apellidos" to binding.apellidosP.text.toString(),
                                            "gmail" to binding.emailP.text.toString()
                                        )
                                    )

                                // Acceder a la pantalla inicioActivity
                                val intent = Intent(this, MainActivity::class.java).apply {
                                    putExtra("nombreusuario", binding.nombreP.text.toString())
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error en el envío del correo de verificación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Error en el registro del nuevo usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Algun campo está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

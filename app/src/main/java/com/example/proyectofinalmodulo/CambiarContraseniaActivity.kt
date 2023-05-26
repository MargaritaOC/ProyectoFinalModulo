package com.example.proyectofinalmodulo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.databinding.ActivityCambiarContraseniaBinding
import com.example.proyectofinalmodulo.databinding.ActivityMainBinding
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore

class CambiarContraseniaActivity : AppCompatActivity() {

    lateinit var binding: ActivityCambiarContraseniaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCambiarContraseniaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = FirebaseAuth.getInstance()

        binding.Bcambiarcontrasenia.setOnClickListener {

            val user = auth.currentUser

            if (binding.emailC.text.toString().isNotEmpty()) {
                    auth.sendPasswordResetEmail(binding.emailC.text.toString())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Se envió el correo electrónico correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, MainActivity::class.java)
                                finishAffinity()
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Error al enviar el correo electrónico, revisar carpeta spam",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, ingresa un correo electrónico válido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }
}



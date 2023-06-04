package com.example.proyectofinalmodulo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.View
import android.widget.Toast
import com.example.proyectofinalmodulo.Models.UsuariosData
import com.example.proyectofinalmodulo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var usuarios_data : UsuariosData
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usuarios_data = UsuariosData()
        val db = Firebase.firestore
        val emailEditable: Editable = Editable.Factory.getInstance().newEditable(usuarios_data.gmail)

        binding.BinicioSesion.setOnClickListener{
            login()
            binding.emailPMain.text = emailEditable

            binding.emailPMain.text.clear()
            binding.passwordPMain.text.clear()
        }

        binding.verPassword.setOnClickListener {
            verPassword()
        }

        binding.BRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }


        binding.TcambiarPassword.setOnClickListener {
            val intent = Intent(this, CambiarContraseniaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(){
        if (binding.emailPMain.text.isNotEmpty() && binding.passwordPMain.text.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.emailPMain.text.toString(),
                binding.passwordPMain.text.toString()
            )
                .addOnCompleteListener { loginTask ->
                    if (loginTask.isSuccessful) {
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null && user.isEmailVerified) {
                            val intent = Intent(this, InicioActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this,
                                "Debes verificar tu correo electrónico antes de iniciar sesión.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Algun campo está vacío", Toast.LENGTH_SHORT).show()
        }

    }

    private fun verPassword() {
        isPasswordVisible = !isPasswordVisible

        if (isPasswordVisible) {
            binding.passwordPMain.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.verPassword.setImageResource(R.drawable.ver_password)
        } else {
            binding.passwordPMain.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.verPassword.setImageResource(R.drawable.ver_password)
        }
    }
}
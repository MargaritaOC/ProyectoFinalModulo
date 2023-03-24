package com.example.proyectofinalmodulo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectofinalmodulo.Menu.MenuSalidaActivity
import com.example.proyectofinalmodulo.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistroActivity : MenuSalidaActivity() {
    lateinit var binding: ActivityRegistroBinding
    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Nuevo usuario" //cambia el titulo de la pantalla
        binding.BregistrarP.setOnClickListener {
            if (binding.emailP.text.isNotEmpty() && binding.passwordPRegistro.text.isNotEmpty()
                && binding.nombreP.text.isNotEmpty() && binding.apellidosP.text.isNotEmpty()){

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.emailP.text.toString(), binding.passwordPRegistro.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful){ //si se han registrado los datos satisfactoriamente


                        db.collection("Usuarios").document(binding.emailP.text.toString())
                            .set(mapOf(
                                "nombre" to binding.nombreP.text.toString(),
                                "apellidos" to binding.apellidosP.text.toString(),

                                ))

                        //accedemos a la pantalla inicioActivity
                        val intent = Intent(this, InicioActivity::class.java).apply {
                            putExtra("nombreusuario", binding.nombreP.text.toString())
                        }
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Error en el registro del nuevo usuario", Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                Toast.makeText(this, "Algun campo está vacío", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
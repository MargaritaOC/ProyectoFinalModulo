package com.example.proyectofinalmodulo

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.proyectofinalmodulo.Menu.MenuSalidaActivity
import com.example.proyectofinalmodulo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : MenuSalidaActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BinicioSesion.setOnClickListener{
            login()
        }
        binding.BRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
        val db = Firebase.firestore
    }

    private fun login(){
        //Si el correo o la contraseña no son campos vacios:
        if (binding.emailPMain.text.isNotEmpty() && binding.passwordPMain.text.isNotEmpty()){
            //Iniciamos sesion con el metodo singIn y enviamos a firebase el correo y la contraseña
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.emailPMain.text.toString(),
                binding.passwordPMain.text.toString()
            )
                .addOnCompleteListener {
                    //Si la autenticacion tuvo exito:
                    if (it.isSuccessful){
                        //Accedemos a la pantalla InicioActivity, para dar la bienvenida al usuario.
                        val intent = Intent(this, InicioActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Correo o password incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this, "Algun campo está vacío", Toast.LENGTH_SHORT).show()
        }
    }
}
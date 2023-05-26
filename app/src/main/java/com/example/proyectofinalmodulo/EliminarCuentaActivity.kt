package com.example.proyectofinalmodulo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.databinding.ActivityMainBinding
import com.example.proyectofinalmodulo.databinding.EliminarCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EliminarCuentaActivity : MenuActivity() {

    lateinit var binding: EliminarCuentaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EliminarCuentaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BeliminarP.setOnClickListener {
            val confirmDialog = AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación de cuenta")
                .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es irreversible.")
                .setPositiveButton("Eliminar") { dialog: DialogInterface, _: Int ->
                    val db = FirebaseFirestore.getInstance()
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    if (currentUser != null) {
                        val userEmailAddress = currentUser?.email

                        // Eliminar el usuario de Firebase Authentication
                        currentUser.delete()
                            .addOnSuccessListener {
                                // La cuenta del usuario se eliminó correctamente de Firebase Authentication

                                // Eliminar el usuario de la colección "Usuario" en Firestore
                                db.collection("Usuarios")
                                    .document(userEmailAddress.toString())
                                    .delete()
                                    .addOnSuccessListener {
                                        // La cuenta del usuario se eliminó correctamente de la base de datos Firestore
                                        Toast.makeText(
                                            this,
                                            "La cuenta se eliminó correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        finishAffinity()
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        // Manejar el error en caso de que no se pueda eliminar la cuenta del usuario de la base de datos Firestore
                                        Toast.makeText(
                                            this,
                                            "Error al eliminar la cuenta",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                            .addOnFailureListener { exception ->
                                // Manejar el error en caso de que no se pueda eliminar la cuenta del usuario de Firebase Authentication
                                Toast.makeText(
                                    this,
                                    "Error al eliminar la cuenta",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        // Manejar el caso cuando el usuario no está autenticado
                        Toast.makeText(
                            this,
                            "Usuario no autenticado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .create()

            confirmDialog.show()
            true
        }
    }

}


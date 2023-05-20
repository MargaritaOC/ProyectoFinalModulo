package com.example.proyectofinalmodulo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html.ImageGetter
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalmodulo.Adapter.MascotaAdapter
import com.example.proyectofinalmodulo.Menu.MenuActivity
import com.example.proyectofinalmodulo.Models.MascotasData
import com.example.proyectofinalmodulo.databinding.ActivityAnadirMascotaBinding
import com.example.proyectofinalmodulo.databinding.ActivityInicioBinding
import com.example.proyectofinalmodulo.databinding.ListadoBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class AnadirMascota : MenuActivity() {
    lateinit var binding: ActivityAnadirMascotaBinding
    lateinit var binding2: ListadoBinding
    lateinit var binding3: ActivityInicioBinding
    lateinit var imagenes: ImageButton
    private lateinit var adapter: MascotaAdapter
    private lateinit var mascotasRecyclerView: RecyclerView
    private lateinit var mascotasArrayList: ArrayList<MascotasData>


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
                binding.subirImagen.setImageBitmap(it)
            }
        } else {
            startActivity(Intent(this, AnadirMascota::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mascotasArrayList = ArrayList()
        adapter = MascotaAdapter(mascotasArrayList)
        mascotasRecyclerView = RecyclerView(applicationContext)
        binding = ActivityAnadirMascotaBinding.inflate(layoutInflater)
        binding2 = ListadoBinding.inflate(layoutInflater)
        binding3 = ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagenes = binding.subirImagen
        binding.subirImagen.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        fun subirIMG(): Task<Uri> {
            val timestamp = System.currentTimeMillis()
            val fileName = "IMGmascotas_$timestamp.jpg"
            val storageRef = FirebaseStorage.getInstance().getReference("//IMGmascotas/$fileName")

            val bitmap = (binding.subirImagen.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val uploadTask = storageRef.putBytes(data)

            return uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageRef.downloadUrl
            }
        }

        val random = Random()
        val limiteMaximo = 1000
        val generatedIds = mutableSetOf<Int>()
        var idUnica = random.nextInt(limiteMaximo)


        while (generatedIds.contains(idUnica)) {
            idUnica = random.nextInt(limiteMaximo)
        }

        generatedIds.add(idUnica)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmailAddress = currentUser?.email

        // metemos la funcion y creamos la nueva mascota

        binding.BregistrarM.setOnClickListener {
            if (binding.chipM.text.isNullOrEmpty() || binding.edadM.text.isNullOrEmpty()
                || binding.nombreM.text.isNullOrEmpty()
                || binding.descripcionM.text.isNullOrEmpty()
                || binding.localidadM.text.isNullOrEmpty() || binding.razaM.text.isNullOrEmpty()
                || binding.duenoM.text.isNullOrEmpty()
                || binding.telefonoM.text.isNullOrEmpty() )
             {
                Toast.makeText(this, "Tiene que ingresar todos los campos", Toast.LENGTH_SHORT)
                    .show()
            } else {


                db.collection("Mascotas")
                    .whereEqualTo("chip", binding.chipM.text.toString())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            // El chip no existe, se puede agregar la mascota




                        // Sube la imagen
                        subirIMG().addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            val data = mapOf(
                                "idM" to idUnica,
                                "chip" to binding.chipM.text.toString(),
                                "nombre" to binding.nombreM.text.toString(),
                                "Estado" to binding.estadoM.selectedItem.toString(),
                                "descripcion" to binding.descripcionM.text.toString(),
                                "edad" to binding.edadM.text.toString(),
                                "localidad" to binding.localidadM.text.toString(),
                                "raza" to binding.razaM.text.toString(),
                                "sexo" to binding.sexoM.selectedItem.toString(),
                                "especie" to binding.especieM.selectedItem.toString(),
                                "vacunado" to binding.vacunadoM.selectedItem.toString(),
                                "esterilizado" to binding.esterilizadoM.selectedItem.toString(),
                                "gmail" to userEmailAddress,
                                "dueño" to binding.duenoM.text.toString(),
                                "telefono" to binding.telefonoM.text.toString(),
                                "imagen" to downloadUrl
                            )

                            // Inserta la mascota en la base de datos
                            db.collection("Mascotas").document(binding.chipM.text.toString()).set(data)
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        this,
                                        "El registro de la nueva mascota se realizó correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    limpiarCampos()
                                    cargarDatos()

                                    val intent = Intent(this, InicioActivity::class.java).apply {
                                        putExtra("nombremascota", binding.nombreM.text.toString())
                                    }
                                    actividadActual = 0
                                    startActivity(intent)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Error en el registro de la nueva mascota",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }.addOnFailureListener { exception ->
                            Log.e("Imagen", "Error al subir la imagen: $exception")
                        }

                    } else {
                    // El chip ya existe en la base de datos
                    Toast.makeText(this, "El chip ya está en uso", Toast.LENGTH_SHORT).show()
                }
            }
                .addOnFailureListener { exception ->
                    // Error al realizar la consulta
                    Toast.makeText(this, "Error al verificar el chip", Toast.LENGTH_SHORT).show()
                    Log.e("Verificación de chip", exception.toString())
                }
        }


        }
            //modificar el boton eliminar cuando sea nulo el chip que salte un error

            binding.BeliminarM.setOnClickListener {
                if (binding.chipM.text.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor, ingrese un valor para el campo Chip",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Confirmar eliminación")
                builder.setMessage("¿Estás seguro de que deseas eliminar esta mascota?")


                // Verificar si el documento existe antes de eliminarlo
                db.collection("Mascotas")
                    .document(binding.chipM.text.toString())
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {

                            builder.setPositiveButton("Si") { dialog, which ->
                                // Acciones a realizar si el usuario confirma la eliminación
                                db.collection("Mascotas")
                                    .document(binding.chipM.text.toString())
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "La mascota se eliminó correctamente",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        limpiarCampos()
                                        cargarDatos()

                                        val intent =
                                            Intent(this, InicioActivity::class.java).apply {
                                                putExtra(
                                                    "nombremascota",
                                                    binding.nombreM.text.toString()
                                                )
                                            }
                                        actividadActual = 0
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { exception ->
                                        val errorMessage =
                                            "Error al eliminar la mascota: ${exception.message}"
                                        Toast.makeText(
                                            this,
                                            errorMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener { exception ->
                                        val errorMessage =
                                            "Error al obtener la mascota: ${exception.message}"
                                        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                            builder.setNegativeButton("No") { dialog, which ->
                                // Acciones a realizar si el usuario no confirma la eliminación

                                limpiarCampos()
                                actividadActual = 1
                            }

                            val dialog = builder.create()
                            dialog.show()
                        } else {
                            // El documento no existe
                            Toast.makeText(
                                this,
                                "La mascota con el chip especificado no existe",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


            }

            //modificar el boton actualizar cuando sea nulo el chip que salte un error
        binding.BactualizarM.setOnClickListener {

                if (binding.chipM.text.isNullOrEmpty() || binding.edadM.text.isNullOrEmpty()
                    || binding.nombreM.text.isNullOrEmpty()
                    || binding.descripcionM.text.isNullOrEmpty()
                    || binding.localidadM.text.isNullOrEmpty() || binding.razaM.text.isNullOrEmpty()
                    || binding.duenoM.text.isNullOrEmpty()
                    || binding.telefonoM.text.isNullOrEmpty()
                ) {
                    Toast.makeText(this, "Tiene que ingresar todos los campos", Toast.LENGTH_SHORT).show()
                } else {

                    db.collection("Mascotas")
                        .document(binding.chipM.text.toString())
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // El documento existe, puedes proceder a actualizarlo
                                subirIMG().addOnSuccessListener { uri ->
                                    val downloadUrl = uri.toString()
                                    val data = mapOf(
                                        "idM" to idUnica,
                                        "chip" to binding.chipM.text.toString(),
                                        "nombre" to binding.nombreM.text.toString(),
                                        "Estado" to binding.estadoM.selectedItem.toString(),
                                        "descripcion" to binding.descripcionM.text.toString(),
                                        "edad" to binding.edadM.text.toString(),
                                        "localidad" to binding.localidadM.text.toString(),
                                        "raza" to binding.razaM.text.toString(),
                                        "sexo" to binding.sexoM.selectedItem.toString(),
                                        "especie" to binding.especieM.selectedItem.toString(),
                                        "vacunado" to binding.vacunadoM.selectedItem.toString(),
                                        "esterilizado" to binding.esterilizadoM.selectedItem.toString(),
                                        "gmail" to userEmailAddress,
                                        "dueño" to binding.duenoM.text.toString(),
                                        "telefono" to binding.telefonoM.text.toString(),
                                        "imagen" to downloadUrl
                                    )
                                    db.collection("Mascotas")
                                        .document(binding.chipM.text.toString())
                                        .set(data)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "La mascota se actualizó correctamente",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            limpiarCampos()
                                            cargarDatos()

                                            val intent =
                                                Intent(this, InicioActivity::class.java).apply {
                                                    putExtra(
                                                        "nombremascota",
                                                        binding.nombreM.text.toString()
                                                    )
                                                }
                                            actividadActual = 0
                                            startActivity(intent)

                                        }
                                        .addOnFailureListener { exception ->
                                            val errorMessage =
                                                "Error al actualizar la mascota: ${exception.message}"
                                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                // El documento no existe
                                Toast.makeText(
                                    this,
                                    "La mascota con el chip especificado no existe",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

        }


            binding.botonAutoCompletar.setOnClickListener {

                if (binding.chipM.text.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Por favor, ingrese un valor para el campo Chip",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                db.collection("Mascotas")
                    .whereEqualTo("chip", binding.chipM.text.toString())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val mascota = querySnapshot.documents.first()

                            val opciones = resources.getStringArray(R.array.opciones_array)
                            val especies = resources.getStringArray(R.array.especies_mascotas)
                            val sexos = resources.getStringArray(R.array.sexo_mascotas)
                            val adopciones = resources.getStringArray(R.array.adopciones_mascotas)

                            // Obtén los valores de la base de datos
                            val opcionVacuna = mascota.getString("vacunado")
                            val opcionEsterilizado = mascota.getString("esterilizado")
                            val opcionSexo = mascota.getString("sexo")
                            val opcionEstado = mascota.getString("Estado")
                            val opcionEspecie = mascota.getString("especie")

                            // Asigna las opciones a los spinners
                            binding.vacunadoM.setSelection(opciones.indexOf(opcionVacuna))
                            binding.esterilizadoM.setSelection(opciones.indexOf(opcionEsterilizado))
                            binding.sexoM.setSelection(sexos.indexOf(opcionSexo))
                            binding.estadoM.setSelection(adopciones.indexOf(opcionEstado))
                            binding.especieM.setSelection(especies.indexOf(opcionEspecie))

                            binding.nombreM.setText(mascota.getString("nombre"))
                            binding.descripcionM.setText(mascota.getString("descripcion"))
                            binding.edadM.setText(mascota.getString("edad"))
                            binding.localidadM.setText(mascota.getString("localidad"))
                            binding.razaM.setText(mascota.getString("raza"))
                            binding.duenoM.setText(mascota.getString("dueño"))
                            binding.telefonoM.setText(mascota.getString("telefono"))

                            val urlImagen = mascota.getString("imagen")

                            Glide.with(this)
                                .load(urlImagen)
                                .into(binding.subirImagen)
                        } else {
                            Toast.makeText(
                                this,
                                "No se encontró ninguna mascota ese chip ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }

        binding.botonfoto.setOnClickListener {
            pickFoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
        }

        private fun limpiarCampos() {
            binding.chipM.text.clear()
            binding.nombreM.text.clear()
            binding.estadoM.setSelection(0)
            binding.descripcionM.text.clear()
            binding.edadM.text.clear()
            binding.localidadM.text.clear()
            binding.razaM.text.clear()
            binding.sexoM.setSelection(0)
            binding.especieM.setSelection(0)
            binding.vacunadoM.setSelection(0)
            binding.esterilizadoM.setSelection(0)
            binding.duenoM.text.clear()
            binding.telefonoM.text.clear()
            binding.subirImagen.setImageResource(R.drawable.subir_imagen)
        }

        private fun cargarDatos() {


            val db = FirebaseFirestore.getInstance()
            // Obtengo los datos de la base de datos
            db.collection("Mascotas")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("Añadiendo Mascotas", "${document.id} => ${document.data}")
                        val mascota = document.toObject(MascotasData::class.java)
                        mascotasArrayList.add(mascota)
                        // Muestro el recyclerView
                        binding3.mascotasLista.layoutManager = LinearLayoutManager(this)
                        binding3.mascotasLista.adapter = MascotaAdapter(mascotasArrayList)
                        // Vuelvo a actualizar el adapter para el borrado
                        var adapter = MascotaAdapter(mascotasArrayList)
                        mascotasRecyclerView?.adapter = adapter
                        val position = mascotasArrayList.indexOf(MascotasData())
                        adapter.notifyItemRemoved(position)
                        adapter.notifyDataSetChanged()


                    }


                }   }
        }









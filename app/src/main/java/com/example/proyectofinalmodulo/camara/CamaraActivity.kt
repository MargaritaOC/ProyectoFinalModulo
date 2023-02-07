package com.example.proyectofinalmodulo.camara

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.proyectofinalmodulo.databinding.ActivityCamaraBinding

class CamaraActivity : AppCompatActivity() {
    lateinit var imagenes: ImageButton
    lateinit var binding: ActivityCamaraBinding

    val pickFoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        val foto = it.data?.extras?.get("data") as Bitmap
        binding.imagen.setImageBitmap(foto)
    }
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
            uri ->
        if(uri!=null){
            imagenes.setImageURI(uri)
        }else{

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCamaraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imagenes= binding.imagen

        binding.BAgaleria.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.BAcamara.setOnClickListener{
            pickFoto.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        }
    }
}
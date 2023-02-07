package com.example.proyectofinalmodulo.Menu

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectofinalmodulo.AnadirMascota
import com.example.proyectofinalmodulo.InicioActivity
import com.example.proyectofinalmodulo.MainActivity
import com.example.proyectofinalmodulo.R
import com.example.proyectofinalmodulo.camara.CamaraActivity

open class MenuActivity : AppCompatActivity() {
    companion object{
        var actividadActual = 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.activity_menu,menu)

        for (i in 0 until menu.size()){
            if (i== actividadActual) menu.getItem(i).isEnabled = false
            else menu.getItem(i).isEnabled = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.mascotas -> {

                val intent = Intent(this, InicioActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                actividadActual = 0
                startActivity(intent)
                true


            }

            R.id.Añadir_mascota ->{

                val intent = Intent(this, AnadirMascota::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                actividadActual = 1
                startActivity(intent)
                true

            }

            R.id.salirApli ->{

                val intent = Intent(this, MainActivity::class.java)
                actividadActual = 2
                startActivity(intent)
                true

            }

            R.id.accesoCamara ->{

                val intent = Intent(this, CamaraActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                actividadActual = 3
                startActivity(intent)
                true

            }

            else -> super.onOptionsItemSelected(item)
        }

    }
}
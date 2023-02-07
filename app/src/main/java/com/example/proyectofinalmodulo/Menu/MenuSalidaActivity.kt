package com.example.proyectofinalmodulo.Menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.proyectofinalmodulo.InicioActivity
import com.example.proyectofinalmodulo.R

open class MenuSalidaActivity : AppCompatActivity() {
    companion object{
        var actividadActual = 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.salida,menu)

        for (i in 0 until menu.size()){
            if (i== actividadActual) menu.getItem(i).isEnabled = false
            else menu.getItem(i).isEnabled = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.salir -> {

                val intent = Intent(this, Phone::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                true


            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
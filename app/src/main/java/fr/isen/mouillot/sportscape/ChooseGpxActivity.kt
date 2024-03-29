package fr.isen.mouillot.sportscape

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ChooseGpxActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_gpx)

        val listView = findViewById<ListView>(R.id.listViewGpxFiles)
        val gpxFiles = assets.list("gpx")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gpxFiles!!)
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val chosenFile = gpxFiles[position]
            val returnIntent = Intent()
            returnIntent.putExtra("chosenGpxFile", chosenFile)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}

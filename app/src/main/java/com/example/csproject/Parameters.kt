package com.example.csproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.AsyncTask
import android.widget.EditText
import org.json.JSONObject
import java.net.URL

interface DataListener {
    fun onDataReceived(result: String?)
}

class Parameters : AppCompatActivity(), DataListener {

    private val getWeather = GetWeather()
    private val api: String = "8020ae170364761c9bab565a437b0b21"
    var latitude = 0.0
    var longitude = 0.0

    var rainfall = "0.0"
    var humidity = ""
    var temperature = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        val simpleSwitch = findViewById<SwitchCompat>(R.id.WeatherSoilSwitch)
        simpleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                Toast.makeText(this, "View Weather", Toast.LENGTH_SHORT).show()
                findViewById<LinearLayout>(R.id.soilLayout).visibility = View.GONE
                findViewById<LinearLayout>(R.id.weatherLayout).visibility = View.VISIBLE
            }else{
                Toast.makeText(this, "View Soil", Toast.LENGTH_SHORT).show()
                findViewById<LinearLayout>(R.id.weatherLayout).visibility = View.GONE
                findViewById<LinearLayout>(R.id.soilLayout).visibility = View.VISIBLE
            }
        }
        simpleSwitch.textOn = "Weather"
        simpleSwitch.textOff = "Soil"

        getWeather.setDataListener(this)
        getWeather.execute()

        val output = findViewById<TextView>(R.id.btnPredict)
        output.setOnClickListener{
            val intent = Intent(this, PredictedOutput::class.java)

            val phValue = findViewById<EditText>(R.id.pH).text
            val nitrogenValue = findViewById<EditText>(R.id.nitrogen).text
            val phosphorousValue = findViewById<EditText>(R.id.phosphorous).text
            val potassiumValue = findViewById<EditText>(R.id.potassium).text

            intent.putExtra("pH", "$phValue")
            intent.putExtra("nitrogen", "$nitrogenValue")
            intent.putExtra("phosphorous", "$phosphorousValue")
            intent.putExtra("potassium", "$potassiumValue")
            intent.putExtra("rainfall", "$rainfall")
            intent.putExtra("temperature", "$temperature")
            intent.putExtra("humidity", "$humidity")

            startActivity(intent)
            finish()
        }
    }

    override fun onDataReceived(result: String?) {
        // Handle the data received from the inner class

        val main = JSONObject(result).getJSONObject("main")
        //rainfall = JSONObject(result).getJSONObject("rain").getString("1h")
        temperature = main.getString("temp")
        humidity = main.getString("humidity")

        findViewById<EditText>(R.id.rainfall).setText("$rainfall"+" mm")
        findViewById<EditText>(R.id.rainfall).isEnabled = false

        findViewById<EditText>(R.id.temperature).setText(temperature + " °C")
        findViewById<EditText>(R.id.temperature).isEnabled = false

        findViewById<EditText>(R.id.humidity).setText(humidity + " %")
        findViewById<EditText>(R.id.humidity).isEnabled = false

    }

    inner class GetWeather() : AsyncTask<String, Void, String>(){

        private var dataListener: DataListener? = null

        fun setDataListener(listener: DataListener) {
            dataListener = listener
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$api&units=metric")
                    .readText(Charsets.UTF_8)
            }
            catch(e : Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                dataListener?.onDataReceived(result)
            }catch (e: Exception){
                println(e.printStackTrace())
            }
        }
    }
}
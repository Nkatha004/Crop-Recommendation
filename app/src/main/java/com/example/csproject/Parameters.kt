package com.example.csproject

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.os.AsyncTask
import android.widget.EditText
import org.json.JSONObject
import java.net.URL
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface DataListener {
    fun onDataReceived(averagesResult: String?, weatherResult: String?)
}

class Parameters : AppCompatActivity(), DataListener {

    private val getWeather = GetWeather()
    //world weather online
    private val api: String = "82d2da52022d4904a58103506232010"

    //open weather api
    private val apiOpenweather: String = "8020ae170364761c9bab565a437b0b21"
    private var isTaskExecuted = false
    var latitude = 0.0
    var longitude = 0.0

    var humidity = ""
    var temperature = ""
    var rainfall = ""
    var potassium = ""
    var nitrogen = ""
    var phosphorous = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parameters)
        FirebaseApp.initializeApp(this);

        latitude = intent.getDoubleExtra("latitude", 0.0)
        longitude = intent.getDoubleExtra("longitude", 0.0)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Welcome!")
        builder.setMessage("Kindly input the soil pH only as obtained from your sensor, all other fields will be auto-populated. Thank you!")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()

        val simpleSwitch = findViewById<SwitchCompat>(R.id.WeatherSoilSwitch)
        simpleSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                findViewById<LinearLayout>(R.id.soilLayout).visibility = View.GONE
                findViewById<LinearLayout>(R.id.weatherLayout).visibility = View.VISIBLE
            }else{
                findViewById<LinearLayout>(R.id.weatherLayout).visibility = View.GONE
                findViewById<LinearLayout>(R.id.soilLayout).visibility = View.VISIBLE
            }
        }
        simpleSwitch.textOn = "Weather"
        simpleSwitch.textOff = "Soil"

        if(!isTaskExecuted){
            try{
                getWeather.setDataListener(this)
                getWeather.execute()
            }catch (e: Exception){
                e.printStackTrace()
            }

            isTaskExecuted = true
        }

        //firebase ops
        // Call the function to retrieve the last values using coroutines
        lifecycleScope.launch {
            phosphorous = getSoilParams("phosphorous").toString()
            nitrogen = getSoilParams("nitrogen").toString()
            potassium = getSoilParams("potassium").toString()

            findViewById<EditText>(R.id.nitrogen).setText(nitrogen + " mg/kg")
            findViewById<EditText>(R.id.nitrogen).isEnabled = false

            findViewById<EditText>(R.id.potassium).setText(potassium + " mg/kg")
            findViewById<EditText>(R.id.potassium).isEnabled = false

            findViewById<EditText>(R.id.phosphorous).setText(phosphorous + " mg/kg")
            findViewById<EditText>(R.id.phosphorous).isEnabled = false
        }

        val output = findViewById<TextView>(R.id.btnPredict)
        output.setOnClickListener{
            val intent = Intent(this, PredictedOutput::class.java)
            val phValue = findViewById<EditText>(R.id.pH).text
            val builder = AlertDialog.Builder(this)

            if(phValue.isNotEmpty()){
                if(phValue.toString().toInt() <= 0){
                    builder.setTitle("Soil pH Value")
                    builder.setMessage("Sorry, soil pH should be a value greater than 0 i.e. 1 and above!")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }else if(phValue.toString().toInt() > 14){
                    builder.setTitle("Soil pH Value")
                    builder.setMessage("Sorry, soil pH should be a value less than 15 i.e. 14 and below!")
                    builder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    builder.show()
                }else{
                    intent.putExtra("pH", "$phValue")
                    intent.putExtra("nitrogen", "$nitrogen")
                    intent.putExtra("phosphorous", "$phosphorous")
                    intent.putExtra("potassium", "$potassium")
                    intent.putExtra("rainfall", "$rainfall")
                    intent.putExtra("temperature", "$temperature")
                    intent.putExtra("humidity", "$humidity")

                    startActivity(intent)
                    finish()
                }
            }else{
                builder.setTitle("Soil pH Value")
                builder.setMessage("Sorry, soil pH value is required!")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    private suspend fun getSoilParams(element: String): Int = suspendCoroutine { continuation ->
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("npk")
        val elementReference = databaseReference.child(element)

        elementReference.orderByKey().limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Check if there is any data
                    if (dataSnapshot.exists()) {
                        // Iterate through the dataSnapshot to get the last entry
                        for (childSnapshot in dataSnapshot.children) {
                            val lastValue = childSnapshot.getValue(Int::class.java)
                            continuation.resume(lastValue ?: 0)
                            return
                        }
                    }
                    continuation.resume(0)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Failed to retrieve last $element value: ${databaseError.message}")
                    continuation.resume(0)
                }
            })
    }

    override fun onDataReceived(averagesResult: String?, weatherResult: String?){

        //Handle rainfall averages from world weather online
        val jsonObject = JSONObject(averagesResult)
        val climateAverages = jsonObject.getJSONObject("data").getJSONArray("ClimateAverages")

        // Initialize variables for calculating the average
        var totalRainfall = 0.0
        var numMonths = 0

        // Loop through the months and calculate the total rainfall
        for (i in 0 until climateAverages.length()) {
            val month = climateAverages.getJSONObject(i)
            val avgDailyRainfall = month.getJSONArray("month").getJSONObject(0).getString("avgDailyRainfall").toDouble()
            totalRainfall += avgDailyRainfall
            numMonths++
        }

        // Calculate the average rainfall
        rainfall = (totalRainfall / numMonths).toString()

        // Handle the data received from the open weather API with the weather results
        val main = JSONObject(weatherResult).getJSONObject("main")
        temperature = main.getString("temp")
        humidity = main.getString("humidity")

        findViewById<EditText>(R.id.rainfall).setText(rainfall + " mm")
        findViewById<EditText>(R.id.rainfall).isEnabled = false

        findViewById<EditText>(R.id.temperature).setText(temperature + " °C")
        findViewById<EditText>(R.id.temperature).isEnabled = false

        findViewById<EditText>(R.id.humidity).setText(humidity + " %")
        findViewById<EditText>(R.id.humidity).isEnabled = false

    }

    inner class GetWeather() : AsyncTask<Void, Void, Pair<String?, String?>>() {

        private var dataListener: DataListener? = null

        fun setDataListener(listener: DataListener) {
            dataListener = listener
        }

        override fun doInBackground(vararg params: Void?): Pair<String?, String?> {
            var averageResponse: String?
            var weatherResponse: String?

            try{
                //obtain the monthly averages
                averageResponse = URL("https://api.worldweatheronline.com/premium/v1/weather.ashx?key=$api&q=$latitude,$longitude&fx=no&cc=no&mca=yes&format=json&includelocation=yes")
                    .readText(Charsets.UTF_8)

                //obtain other weather values
                weatherResponse = URL("https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&appid=$apiOpenweather&units=metric")
                    .readText(Charsets.UTF_8)
            }
            catch(e : Exception) {
                e.printStackTrace()
                averageResponse = null
                weatherResponse = null
            }

            return Pair(averageResponse, weatherResponse)
        }

        override fun onPostExecute(result: Pair<String?, String?>) {
            try{
                val (averageResponse, weatherResponse) = result
                dataListener?.onDataReceived(averageResponse, weatherResponse)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
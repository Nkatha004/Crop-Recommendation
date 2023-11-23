package com.example.csproject

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley

class PredictedOutput : AppCompatActivity() {
    val url = "https://crop-recommendation-blond.vercel.app/predict"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predicted_output)

        val ph = intent.getStringExtra("pH")
        val nitrogen = intent.getStringExtra("nitrogen")
        val potassium = intent.getStringExtra("potassium")
        val phosphorous = intent.getStringExtra("phosphorous")
        val rainfall = intent.getStringExtra("rainfall")
        val humidity = intent.getStringExtra("humidity")
        val temperature = intent.getStringExtra("temperature")

        System.out.println("Nitrogen $nitrogen")
        System.out.println("Phosphorous $phosphorous")
        System.out.println("Potassium $potassium")
        System.out.println("PH $ph")


        // Create a Volley request queue
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        // Create a StringRequest for a POST request
        val stringRequest = object : StringRequest(Request.Method.POST, url,
            Response.Listener { response ->
                val response = response.toString()

                try {
                    val jsonObject = JSONObject(response)
                    val result = jsonObject.getString("result")
                    findViewById<TextView>(R.id.crop).text = result

                } catch (e: Exception) {
                    // Handle any parsing or JSON-related exceptions
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                println(error.message)
            }) {

            // Set the request body as form data
            override fun getBodyContentType(): String {
                return "application/x-www-form-urlencoded; charset=UTF-8"
            }

            // Set the form data parameters
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params["ph"] = "$ph"
                params["phosphorous"] = "$phosphorous"
                params["nitrogen"] = "$nitrogen"
                params["potassium"] = "$potassium"
                params["rainfall"] = "$rainfall"
                params["humidity"] = "$humidity"
                params["temperature"] = "$temperature"

                return params
            }
        }

        // Add the request to the queue
        requestQueue.add(stringRequest)

        val home = findViewById<TextView>(R.id.btnHome)
        home.setOnClickListener{
            val intent = Intent(this, LocationInput::class.java)
            Toast.makeText(this, "Thank you!", Toast.LENGTH_SHORT)
            startActivity(intent)
            finish()
        }
    }
}
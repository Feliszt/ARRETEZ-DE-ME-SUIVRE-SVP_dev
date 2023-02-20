package com.plcoding.backgroundlocationtracking

import android.R
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONException
import org.json.JSONObject
import kotlin.random.Random


class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_popup_reminder)
            .setOngoing(true)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                val updatedNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                Log.d("location-data", location.toString())

                //val r = Random
                //val randomLong: Double = 5.69 + (5.72 - 5.69) * r.nextDouble()
                //val randomLat: Double = 45.17 + (45.19 - 45.17) * r.nextDouble()
                //sendJsonPostRequest(randomLong, randomLat, System.currentTimeMillis()/1000)
                sendJsonPostRequest(location.longitude, location.latitude, System.currentTimeMillis()/1000)


                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun sendJsonPostRequest(lon :Double, lat : Double, timestamp : Long) {
        try {

            // Make new json object and put params in it
            val jsonParams = JSONObject()
            jsonParams.put("lon", lon)
            jsonParams.put("lat", lat)
            jsonParams.put("timestamp", timestamp)


            // Building a request
            val request = JsonObjectRequest(
                Request.Method.POST,
                String.format("https://felixcote.fr/receivelocation.php"),
                jsonParams,
                object : Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {
                        // Handle the response
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {
                        // Handle the error
                    }
                })

            /*

              For the sake of the example I've called newRequestQueue(getApplicationContext()) here
              but the recommended way is to create a singleton that will handle this.

              Read more at : https://developer.android.com/training/volley/requestqueue

              Category -> Use a singleton pattern

            */Volley.newRequestQueue(applicationContext).add(request)
        } catch (ex: JSONException) {
            // Catch if something went wrong with the params
        }
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
package com.joaoleite.fortnitecompanion

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONTokener

class MainActivity : AppCompatActivity() {

    private val apiGetUrl = "https://fortnite-public-api.theapinetwork.com/prod09/users/id"
    private val apiStatsUrl = "https://fortnite-public-api.theapinetwork.com/prod09/users/public/br_stats"
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        StrictMode.setThreadPolicy(policy)

        btnSearch.setOnClickListener{
            val inputManager:InputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)

            if(!txtUsername.text.isNullOrEmpty()){
                var player = Player( txtUsername.text.toString())
                getPlatform(player)

                if (!player.platform.isNullOrEmpty()) {
                    getPlayerID(player)

                    if (!player.uid.isNullOrEmpty())
                        getPlayerStats(player)
                }else{
                    Toast.makeText(this, Messages.ERROR_PLATFORM_NOT_SELECTED.message, Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, Messages.ERROR_NO_USERNAME.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPlayerStats(player: Player) {
        Toast.makeText(this, Messages.FETCHING_STATS.message, Toast.LENGTH_SHORT).show()
        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("user_id", player.uid)
                .addFormDataPart("platform", player.platform)
                .addFormDataPart("window", "alltime")
                .build()

        val request = Request.Builder()
                .url(apiStatsUrl)
                .addHeader("Authorization","token" /* INSERT YOUR TOKEN HERE */)
                .post(body)
                .build()

        val response = client.newCall(request).execute().body()?.string()
        val result = JSONObject(JSONTokener(response))

        if(result.has("error")){
            Toast.makeText(this, Messages.ERROR_STATS.message, Toast.LENGTH_LONG).show()
        }else{
            var totals = result.getJSONObject("totals")
            var string = "Player: ${player.username}\n" +
                    "Global Wins: ${totals.get("wins")}\n" +
                    "Global Win Rate: ${totals.get("winrate")}\n" +
                    "Global Kills: ${totals.get("kills")}\n" +
                    "Global KDR: ${totals.get("kd")}"

            lblTest.text = string
        }

    }

    private fun getPlayerID(player: Player) {
        Toast.makeText(this, Messages.FETCHING_UID.message, Toast.LENGTH_SHORT).show()

        val body = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", player.username)
                .build()

        val request = Request.Builder()
                .url(apiGetUrl)
                .addHeader("Authorization", "token"/* INSERT YOUR TOKEN HERE */)
                .post(body)
                .build()

        val response = client.newCall(request).execute().body()?.string()
        val result = JSONObject(JSONTokener(response))

        if(result.has("error")){
            Toast.makeText(this, Messages.ERROR_UID.message, Toast.LENGTH_LONG).show()
        }else{
            player.uid = result.get("uid").toString()
        }
    }

    private fun getPlatform(player: Player){
        if (rdoGroup.checkedRadioButtonId != -1){
            player.platform = findViewById<RadioButton>(rdoGroup.checkedRadioButtonId).text.toString().toLowerCase()
        }
    }
}
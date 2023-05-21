package io.github.sceneview.sample.arcloudanchor

import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ApiTask : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg p0: String): String {

        val index = p0[0]
        var result = ""
        try {
            var url: URL
            var urlConnection: HttpURLConnection? = null

            try {
                url = URL("http://192.168.1.10:8000/customers/$index/orders/$index/products")
                //open a URL coonnection
                urlConnection = url.openConnection() as HttpURLConnection
                val `in`: InputStream = urlConnection.inputStream
                val isw = InputStreamReader(`in`)
                var data: Int = isw.read()
                while (data != -1) {
                    result += data.toChar()
                    data = isw.read()
                }
            }
            catch (e: Exception) {
                Log.d("debug_api", "ça a merdé :$e")
                e.printStackTrace()
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }
    override fun onPreExecute() {
        super.onPreExecute()
        // display a progress dialog for good user experiance

    }

    override fun onPostExecute(json: String?){

        // dismiss the progress dialog after receiving data from API
        try {

            val jsonArray = JSONArray(json)


            for (i in 0..jsonArray.length()-1) {
                Activity.globalJSON.put(jsonArray[i])
            }
            Log.d("debug_api", "fini :")

        } catch (e: JSONException) {
            Log.d("debug_api", "errer : $e")
            e.printStackTrace()
        }
        Activity.flag_api_tast = true
    }
}
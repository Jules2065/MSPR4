package io.github.sceneview.sample.arcloudanchor

import android.app.ProgressDialog
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class ApiTask : AsyncTask<String, String, String>() {
    override fun doInBackground(vararg p0: String): String {
        var result = ""
        try {
            val url: URL
            var urlConnection: HttpURLConnection? = null
            try {
                Log.d("debug_api", "nique")
                url = URL("http://127.0.0.1:8000/docs#/default/customers/1/orders")
                //open a URL coonnection
                urlConnection = url.openConnection() as HttpURLConnection
                val `in`: InputStream = urlConnection.inputStream
                val isw = InputStreamReader(`in`)
                var data: Int = isw.read()
                while (data != -1) {
                    result += data.toChar()
                    data = isw.read()
                }

                // return the data to onPostExecute method
                Log.d("debug_api", "result :" + result)
                return result
            } catch (e: Exception) {
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
        Toast.makeText(Activity.appContext, "processing results", Toast.LENGTH_LONG).show()
    }

    override fun onPostExecute(s: String?) {

        // dismiss the progress dialog after receiving data from API
        try {
            Log.d("debug_api", "s$s")
            val jsonObject = JSONObject(s)
            val jsonArray1 = jsonObject.getJSONArray("users")
            //val jsonObject1 = jsonArray1.getJSONObject(index_no)
            /*val id = jsonObject1.getString("id")
            val name = jsonObject1.getString("name")
            val my_users = "User ID: $id\nName: $name"

            Log.d("debug_api", my_users)*/
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
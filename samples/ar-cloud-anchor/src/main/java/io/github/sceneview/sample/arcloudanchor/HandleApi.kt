package io.github.sceneview.sample.arcloudanchor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONObject

class HandleApi : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.handle_api)


        val dictProducts = mutableMapOf<String, MutableMap<String, String>>()
        val scope = CoroutineScope(Dispatchers.Main)
        val listProduct: LinearLayout = findViewById(R.id.list_api_products)
        val listCarac: RelativeLayout = findViewById(R.id.list_carac)
        val textHeader: TextView = findViewById(R.id.text_header)
        val time: TextView = findViewById(R.id.carac_time)
        val price: TextView = findViewById(R.id.carac_price)
        val descr: TextView = findViewById(R.id.carac_descr)
        val stock: TextView = findViewById(R.id.carac_stock)
        val show3D: Button = findViewById(R.id.show_3d)
        val returnListProducts: Button = findViewById(R.id.return_list_products)

        show3D.setOnClickListener {
            val myIntent = Intent(this, HandleAr::class.java)
            this.startActivity(myIntent)
        }
        returnListProducts.setOnClickListener {
            textHeader.text = "Liste des produits"
            listProduct.visibility = View.VISIBLE
            listCarac.visibility = View.GONE
        }

        scope.launch {
            for (i in 7..20) {
                val result = withContext(Dispatchers.IO) {
                    Activity.flag_api_tast = false
                    val apiTask = ApiTask()
                    apiTask.execute(i.toString())

                    while (!Activity.flag_api_tast);
                }


                }
                withContext(Dispatchers.Main) {
                    for (i in 0 until Activity.globalJSON.length()) {
                        val jsonObject: JSONObject = Activity.globalJSON.get(i) as JSONObject

                        //On remplit le dictionnaire avec un dictionnaire
                        val dictCarac = mutableMapOf<String, String>()
                        dictCarac["creationTime"] = jsonObject.get("createdAt") as String
                        val jsonObjectDetails: JSONObject = jsonObject.get("details") as JSONObject

                        dictCarac["price"] = jsonObjectDetails.get("price") as String
                        dictCarac["description"] = jsonObjectDetails.get("description") as String
                        dictCarac["stock"] = jsonObject.get("stock").toString()
                        dictProducts[jsonObject.get("name") as String] = dictCarac
                    }

                    for ((key, product) in dictProducts){
                        val button = Button(this@HandleApi)
                        button.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        val buttonClickListener = View.OnClickListener { view ->
                            if (view is Button) {
                                //On récupère le texte du bouton et on s'en sert pour afficher ses caractéristiques
                                textHeader.text = "Caractéristiques du produit"
                                listProduct.visibility = View.GONE
                                listCarac.visibility = View.VISIBLE
                                time.text = "Date de création : " + dictProducts[view.text.toString()]!!["creationTime"]
                                price.text = "Prix : " + dictProducts[view.text.toString()]!!["price"] + " Euros"
                                descr.text = "Description : " + dictProducts[view.text.toString()]!!["description"]
                                stock.text = "Stock : " + dictProducts[view.text.toString()]!!["stock"]
                            }
                        }

                        button.setOnClickListener(buttonClickListener)
                        button.text = key

                        // Add the button to the UI layout
                        listProduct.addView(button)
                    }

                }
            }
        // Process the result if needed




    }

}
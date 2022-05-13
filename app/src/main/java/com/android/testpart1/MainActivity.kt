package com.android.testpart1

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.testpart1.databinding.ActivityMainBinding
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE


class MainActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityMainBinding

    private var AdapterXML: ArrayAdapter<String>? = null
    private val ListitemXML = ArrayList<String>()
    private var Results: SoapPrimitive? = null

    private val URL = "https://orapiweb.pttor.com/oilservice/OilPrice.asmx"
    private val NAMESPACE = "http://www.pttor.com"
    private val METHOD_NAME = "CurrentOilPrice"
    private val SOAP_ACTION = "http://www.pttor.com/CurrentOilPrice"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.getWebServiceXML.setOnClickListener {
            GetResponseXML()
        }
    }

    fun GetResponseXML() {
        val s: Thread = object : Thread() {
            override fun run() {
                try {
                    val request = SoapObject(NAMESPACE, METHOD_NAME)
                    request.addProperty("Language", "TH")

                    val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
                    envelope.dotNet = true
                    envelope.setOutputSoapObject(request)

                    val androidHttpTransport = HttpTransportSE(URL)
                    androidHttpTransport.call(SOAP_ACTION, envelope)

                    val resultRequestSOAP = envelope.response as SoapPrimitive
                    Results = resultRequestSOAP

                    Log.e("WebServiceExample", "Response : $Results")

                } catch (e: Exception) {
                    Results = null
                } finally {
                    if (Results == null) {
                        Log.e("WebServiceExample", "Soap object Error")
                    } else {
                        setListItem()
                    }
                }
            }
        }
        s.start()
    }

    fun setListItem() {
        ListitemXML.add(Results.toString())
        val run = Runnable {
            AdapterXML = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, ListitemXML)
            AdapterXML?.notifyDataSetChanged()
            viewBinding.listItem.adapter = AdapterXML
        }
        runOnUiThread(run)
    }
}
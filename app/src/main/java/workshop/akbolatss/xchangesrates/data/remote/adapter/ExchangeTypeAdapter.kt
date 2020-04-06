package workshop.akbolatss.xchangesrates.data.remote.adapter

import android.util.ArrayMap
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import org.json.JSONObject
import workshop.akbolatss.xchangesrates.data.remote.model.ExchangeResponse
import java.lang.reflect.Type
import java.util.*

class ExchangeTypeAdapter : JsonDeserializer<ExchangeResponse> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ExchangeResponse? {
        var exchangeModel: ExchangeResponse? = null
        val jsonObject: JSONObject?
        try {
            jsonObject = JSONObject(json.toString())
            val iterator = jsonObject.keys()
            iterator.next() // skip id
            iterator.next() // skip caption

            val listMap = ArrayMap<String, List<String>>()
            var buffCurrency: MutableList<String>?
            var buff = ""
            while (iterator.hasNext()) {
                buff = iterator.next().toString()

                buffCurrency = ArrayList()
                for (i in 0 until jsonObject.getJSONArray(buff).length()) {
                    buffCurrency.add(jsonObject.getJSONArray(buff).getString(i))
                }
                listMap[buff] = buffCurrency
            }

            exchangeModel =
                ExchangeResponse(
                    jsonObject.getString("ident"),
                    jsonObject.getString("caption"),
                    listMap
                )
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }

        return exchangeModel
    }

}

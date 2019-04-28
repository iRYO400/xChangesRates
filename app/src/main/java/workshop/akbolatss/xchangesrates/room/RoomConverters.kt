package workshop.akbolatss.xchangesrates.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.reflect.TypeToken
import workshop.akbolatss.xchangesrates.model.response.ChartItem
import workshop.akbolatss.xchangesrates.networking.RetrofitInstance

object RoomConverters {

    @TypeConverter
    @JvmStatic
    fun fromCharts(charts: ArrayList<ChartItem>?): String? {
        return RetrofitInstance.gsonInstance().toJson(charts)
    }

    @TypeConverter
    @JvmStatic
    fun toCharts(chartsString: String?): ArrayList<ChartItem>? {
        val listType = object : TypeToken<MutableList<ChartItem>>() {
        }.type
        return RetrofitInstance.gsonInstance().fromJson(chartsString, listType)
    }
}
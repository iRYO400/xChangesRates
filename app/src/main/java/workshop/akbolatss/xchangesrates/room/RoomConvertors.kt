package workshop.akbolatss.xchangesrates.room

import android.arch.persistence.room.TypeConverter
import com.google.gson.reflect.TypeToken
import workshop.akbolatss.xchangesrates.app.ApplicationMain
import workshop.akbolatss.xchangesrates.model.response.ChartItem

object RoomConvertors {

    @TypeConverter
    @JvmStatic
    fun fromCharts(charts: ArrayList<ChartItem>?): String? {
        return ApplicationMain.instance.mGson.toJson(charts)
    }

    @TypeConverter
    @JvmStatic
    fun toCharts(chartsString: String?): ArrayList<ChartItem>? {
        val listType = object : TypeToken<MutableList<ChartItem>>() {
        }.type
        return ApplicationMain.instance.mGson.fromJson(chartsString, listType)
    }
}
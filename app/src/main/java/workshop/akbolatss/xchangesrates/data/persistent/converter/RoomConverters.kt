package workshop.akbolatss.xchangesrates.data.persistent.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.core.KoinComponent
import org.koin.core.inject
import workshop.akbolatss.xchangesrates.model.response.ChartItem

object RoomConverters : KoinComponent {

    private val gson by inject<Gson>()

    @TypeConverter
    @JvmStatic
    fun fromCharts(charts: ArrayList<ChartItem>?): String? {
        return gson.toJson(charts)
    }

    @TypeConverter
    @JvmStatic
    fun toCharts(chartsString: String?): ArrayList<ChartItem>? {
        val listType = object : TypeToken<MutableList<ChartItem>>() {
        }.type
        return gson.fromJson(chartsString, listType)
    }
}

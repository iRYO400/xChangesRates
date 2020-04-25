package workshop.akbolatss.xchangesrates.data.persistent.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.core.KoinComponent
import org.koin.core.inject
import workshop.akbolatss.xchangesrates.data.persistent.model.ChartDotEntity
import workshop.akbolatss.xchangesrates.domain.model.ChangesForPeriod
import workshop.akbolatss.xchangesrates.domain.model.UpdateInterval
import java.math.BigDecimal
import java.util.*

object RoomConverters : KoinComponent {

    private val gson by inject<Gson>()

    @TypeConverter
    @JvmStatic
    fun fromChartItems(charts: List<ChartDotEntity>?): String? {
        return gson.toJson(charts)
    }

    @TypeConverter
    @JvmStatic
    fun toChartItems(chartsString: String?): List<ChartDotEntity>? {
        val listType = object : TypeToken<List<ChartDotEntity>>() {
        }.type
        return gson.fromJson(chartsString, listType)
    }

    @TypeConverter
    @JvmStatic
    fun toUpdateInterval(name: String): UpdateInterval {
        return UpdateInterval.valueOf(name)
    }

    @TypeConverter
    @JvmStatic
    fun fromUpdateInterval(updateInterval: UpdateInterval): String {
        return updateInterval.name
    }

    @TypeConverter
    @JvmStatic
    fun toChangesForPeriod(name: String): ChangesForPeriod {
        return ChangesForPeriod.valueOf(name)
    }

    @TypeConverter
    @JvmStatic
    fun fromChangesForPeriod(changesForPeriod: ChangesForPeriod): String {
        return changesForPeriod.name
    }

    @TypeConverter
    @JvmStatic
    fun fromCurrencies(currencies: Map<String, List<String>>): String {
        return gson.toJson(currencies)
    }

    @TypeConverter
    @JvmStatic
    fun toCurrencies(chartsString: String): Map<String, List<String>> {
        val listType = object : TypeToken<Map<String, List<String>>>() {
        }.type
        return gson.fromJson(chartsString, listType)
    }

    @TypeConverter
    @JvmStatic
    fun toBigDecimal(json: String?): BigDecimal? {
        if (json.isNullOrBlank() || json == "null")
            return null
        return gson.fromJson(json, BigDecimal::class.java)
    }

    @TypeConverter
    @JvmStatic
    fun fromBigDecimal(bigDecimal: BigDecimal?): String? {
        if (bigDecimal == null)
            return null
        return gson.toJson(bigDecimal)
    }

    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long?): Date? {
        if (timestamp == null || timestamp == -1L)
            return null
        return Date(timestamp)
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date?): Long {
        if (date == null)
            return -1
        return date.time
    }

}

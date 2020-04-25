package net.frozendevelopment.frozenvault.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.joda.time.DateTime

class ListConverter {

    @TypeConverter
    fun dateTimeListToJson(value: List<DateTime>?): String {
        val dateConverter = DateConverter()
        val mappedValues: List<Long>? = value?.mapNotNull { dateTime ->
            dateConverter.datetimeToTimeStamp(dateTime)
        }
        return Gson().toJson(mappedValues)
    }

    @TypeConverter
    fun jsonToDateTimeList(value: String?): List<DateTime> {
        val dateConverter = DateConverter()
        val objects = Gson().fromJson(value, Array<Long>::class.java) as Array<Long>
        return objects.mapNotNull { timestamp ->
            dateConverter.timestampToDateTime(timestamp)
        }
    }

}

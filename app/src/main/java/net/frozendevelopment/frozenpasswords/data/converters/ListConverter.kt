package net.frozendevelopment.frozenpasswords.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class ListConverter {

    @TypeConverter
    fun dateListToJson(value: List<Date>?): String {
        val dateConverter = DateConverter()
        val mappedValues: List<Long>? = value?.mapNotNull { date ->
            dateConverter.dateToTimestamp(date)
        }
        return Gson().toJson(mappedValues)
    }

    @TypeConverter
    fun jsonToDateList(value: String?): List<Date> {
        val dateConverter = DateConverter()
        val objects = Gson().fromJson(value, Array<Long>::class.java) as Array<Long>
        return objects.mapNotNull { timestamp ->
            dateConverter.fromTimestamp(timestamp)
        }
    }

}

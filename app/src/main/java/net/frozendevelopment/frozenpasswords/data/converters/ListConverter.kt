package net.frozendevelopment.frozenpasswords.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import net.frozendevelopment.frozenpasswords.data.models.UserModel
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

    @TypeConverter
    fun loginAttemptListToJson(value: List<UserModel.LoginAttempt>): String  = Gson().toJson(value)

    @TypeConverter
    fun jsonToLoginAttemptList(value: String?): List<UserModel.LoginAttempt> {
        val objects = Gson().fromJson(value, Array<UserModel.LoginAttempt>::class.java) as Array<UserModel.LoginAttempt>
        return objects.toList()
    }

}
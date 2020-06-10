package net.frozendevelopment.frozenvault.data.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import net.frozendevelopment.frozenvault.data.models.SecurityQuestionModel

class SecurityQuestionConverter {

    @TypeConverter
    fun securityQuestionToJson(value: List<SecurityQuestionModel>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToSecurityQuestions(value: String?): List<SecurityQuestionModel> {
        value ?: return emptyList()
        return Gson().fromJson(value, Array<SecurityQuestionModel>::class.java).toList()
    }

}

package net.frozendevelopment.frozenvault.data.converters

import androidx.room.TypeConverter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class DateConverter {

    @TypeConverter
    fun datetimeToTimeStamp(dateTime: DateTime?): Long? = dateTime?.toDate()?.time

    @TypeConverter
    fun timestampToDateTime(value: Long?) : DateTime? = value?.let { DateTime(it, DateTimeZone.UTC) }

}

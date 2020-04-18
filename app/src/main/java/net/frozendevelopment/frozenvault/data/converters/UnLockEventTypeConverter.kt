package net.frozendevelopment.frozenvault.data.converters

import androidx.room.TypeConverter
import net.frozendevelopment.frozenvault.data.models.UnlockEventModel

class UnLockEventTypeConverter {

    @TypeConverter
    fun convertFromId(value: Int): UnlockEventModel.UnlockEventType? = UnlockEventModel.UnlockEventType.byEventId(value)

    @TypeConverter
    fun convertToId(value: UnlockEventModel.UnlockEventType): Int = value.eventId

}
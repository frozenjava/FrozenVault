package net.frozendevelopment.frozenpasswords.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName =  "UnlockEvents")
data class UnlockEventModel(
    val eventDate: Date,
    val eventType: UnlockEventType
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    enum class UnlockEventType(val eventId: Int) {
        FAILED(0),
        SUCCESS(1);

        companion object {
            fun byEventId(id: Int): UnlockEventType? = UnlockEventType.values().firstOrNull { it.eventId == id }
        }
    }

}
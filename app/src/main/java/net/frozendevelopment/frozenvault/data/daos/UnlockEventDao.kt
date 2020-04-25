package net.frozendevelopment.frozenvault.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.frozendevelopment.frozenvault.data.models.UnlockEventModel
import org.joda.time.DateTime
import java.util.*

@Dao
abstract class UnlockEventDao {

    @Insert
    abstract suspend fun insert(eventModel: UnlockEventModel)

    @Query("SELECT eventDate FROM UnlockEvents WHERE eventType = :eventType ORDER BY eventDate DESC")
    abstract fun getEventDatesByType(eventType: UnlockEventModel.UnlockEventType): Flow<List<DateTime>>

}

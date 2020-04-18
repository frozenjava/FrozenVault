package net.frozendevelopment.frozenpasswords.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.frozendevelopment.frozenpasswords.data.models.UnlockEventModel
import java.util.*

@Dao
interface UnlockEventDao {

    @Insert
    suspend fun insert(eventModel: UnlockEventModel)

    @Query("SELECT eventDate FROM UnlockEvents WHERE eventType = :eventType")
    fun getEventDatesByType(eventType: UnlockEventModel.UnlockEventType): Flow<List<Date>>

}
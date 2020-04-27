package net.frozendevelopment.frozenvault.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.frozendevelopment.frozenvault.data.models.RecoveryKeyModel
import org.joda.time.DateTime

@Dao
interface RecoveryKeyDao {

    @Query("SELECT * FROM RecoveryKeys")
    fun getAllRecoveryKeys(): Flow<List<RecoveryKeyModel>>

    @Query("SELECT * FROM RecoveryKeys WHERE id = :id")
    suspend fun getRecoveryKeyById(id: Long): RecoveryKeyModel?

    @Query("SELECT COUNT(*) FROM RecoveryKeys WHERE used = 0")
    suspend fun getUnusedKeyCount() : Long

    @Query("UPDATE RecoveryKeys SET used = 1, usedDate = :usedDate WHERE id = :id")
    fun markKeyAsUsed(id: Int, usedDate: DateTime)

    @Insert
    suspend fun insert(recoveryKeyModel: RecoveryKeyModel): Long

    @Query("DELETE FROM RecoveryKeys WHERE id = :id")
    suspend fun delete(id: Int)
}

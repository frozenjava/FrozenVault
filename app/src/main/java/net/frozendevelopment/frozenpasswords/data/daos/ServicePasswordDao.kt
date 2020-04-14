package net.frozendevelopment.frozenpasswords.data.daos

import android.app.Service
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.frozendevelopment.frozenpasswords.data.models.ServicePasswordModel
import java.util.*

@Dao
interface ServicePasswordDao {
    @Query("SELECT * FROM ServicePasswords")
    fun getAllFlow(): Flow<List<ServicePasswordModel>>

    @Query("SELECT * FROM ServicePasswords")
    fun getAllItems(): List<ServicePasswordModel>

    @Query("SELECT * FROM ServicePasswords WHERE id = :id")
    fun getFlowById(id: Int): Flow<ServicePasswordModel>

    @Query("SELECT * FROM ServicePasswords WHERE id = :id")
    fun getItemById(id: Int): ServicePasswordModel

    @Insert
    suspend fun insert(servicePasswordModel: ServicePasswordModel)

    @Update
    suspend fun update(servicePasswordModel: ServicePasswordModel)

    @Query("DELETE FROM ServicePasswords WHERE id = :id")
    suspend fun deleteById(id: Int)
}
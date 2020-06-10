package net.frozendevelopment.frozenvault.data.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.frozendevelopment.frozenvault.data.models.ServicePasswordModel

@Dao
interface ServicePasswordDao {
    @Query("SELECT * FROM ServicePasswords")
    fun getAllFlow(): Flow<List<ServicePasswordModel>>

    @Query("SELECT * FROM ServicePasswords")
    fun getAllItems(): List<ServicePasswordModel>

    @Query("SELECT * FROM ServicePasswords WHERE id = :id")
    fun getFlowById(id: Long): Flow<ServicePasswordModel>

    @Query("SELECT * FROM ServicePasswords WHERE id = :id")
    fun getItemById(id: Long): ServicePasswordModel

    @Insert
    suspend fun insert(servicePasswordModel: ServicePasswordModel): Long

    @Update
    suspend fun update(servicePasswordModel: ServicePasswordModel)

    @Query("DELETE FROM ServicePasswords WHERE id = :id")
    suspend fun deleteById(id: Long)
}

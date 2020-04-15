package net.frozendevelopment.frozenpasswords.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import net.frozendevelopment.frozenpasswords.data.models.UserModel

@Dao
abstract class UserDao {
    @Query("SELECT * FROM User WHERE passwordHash = :hash LIMIT 1")
    abstract suspend fun selectByPasswordHash(hash: String): UserModel

    @Query("SELECT * FROM User LIMIT 1")
    abstract suspend fun getUser(): UserModel

    @Insert
    protected abstract suspend fun insert(userModel: UserModel)

    @Update
    abstract suspend fun update(userModel: UserModel)

    @Query("DELETE FROM User")
    abstract suspend fun nuke()

    suspend fun register(userModel: UserModel) {
        nuke()
        insert(userModel)
    }
}

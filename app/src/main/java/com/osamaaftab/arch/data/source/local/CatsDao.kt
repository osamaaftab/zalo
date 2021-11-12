package com.osamaaftab.arch.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.osamaaftab.arch.domain.entity.Cat
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * Data Access Object for the cats table.
 *
 * Using abstract class (instead of interface) to allow for @Transaction functions
 */
@Dao
abstract class CatsDao {

    /**
     * Select all cats from the cats table.
     */
    @Query("SELECT * FROM cats")
    abstract fun getCats(): Observable<List<Cat>>


    /**
     * Insert a cat in the database. If the cat already exists, replace it.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCat(cat: Cat): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCatSync(cat: Cat)

    /**
     * Delete a cat by id.
     *
     * @param catId the cat id.
     */
    @Query("DELETE FROM cats WHERE id = :catId")
    abstract fun deleteCatById(catId: String): Completable

    /**
     * Bulk insert in a transaction
     */
    @Transaction
    open fun insertCats(cats: List<Cat>) {
        cats.forEach { insertCatSync(it) }
    }
}
package com.osamaaftab.arch.domain.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Immutable model class for a Cat. In order to compile with Room, we can use @JvmOverloads to
 * generate multiple constructors.
 *
 * For the purposes of simplicity, one entity is used for network and database models.
 *
 * @param id          id of the cat
 * @param url       url of the cat
 */
@Entity(tableName = "cats", indices = [Index(value = ["id"], unique = true)])
data class Cat @JvmOverloads constructor(
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val id: String? = null,

    @ColumnInfo(name = "url")
    @SerializedName("url")
    val url: String = "",


) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    var rowId: Long? = null

}
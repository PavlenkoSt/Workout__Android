package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(
    tableName = "records"
)
data class RecordModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "units") val units: RecordUnits,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

class RecordConverters {
    @TypeConverter
    fun fromUnits(units: RecordUnits): String = units.name

    @TypeConverter
    fun toUnits(name: String): RecordUnits = RecordUnits.valueOf(name)
}

enum class RecordUnits(val label: String) {
    REPS("Reps"),
    SEC("Sec"),
    MIN("Min"),
    KM("Km")
}
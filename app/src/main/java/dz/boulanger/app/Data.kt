package dz.boulanger.app

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "plans")
data class ProductionPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val productName: String,
    val units: Int,
    val unitWeightG: Double,
    val flourG: Double,
    val waterG: Double,
    val saltG: Double,
    val yeastG: Double,
)

@Entity(tableName = "stock_notes")
data class StockNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val item: String,
    val qty: String,
    val note: String = "",
)

@Dao
interface BakeryDao {
    @Insert suspend fun insertPlan(plan: ProductionPlan): Long
    @Query("SELECT * FROM plans WHERE dateEpochDay = :day ORDER BY id DESC")
    fun plansForDay(day: Long): Flow<List<ProductionPlan>>
    @Query("DELETE FROM plans WHERE id = :id") suspend fun deletePlan(id: Long)
    @Insert suspend fun insertNote(note: StockNote): Long
    @Query("SELECT * FROM stock_notes ORDER BY createdAt DESC")
    fun allNotes(): Flow<List<StockNote>>
    @Query("DELETE FROM stock_notes WHERE id = :id") suspend fun deleteNote(id: Long)
}

@Database(entities = [ProductionPlan::class, StockNote::class], version = 1, exportSchema = false)
abstract class BakeryDb : RoomDatabase() {
    abstract fun dao(): BakeryDao
}

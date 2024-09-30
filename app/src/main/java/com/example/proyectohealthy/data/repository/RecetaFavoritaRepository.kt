import com.example.proyectohealthy.data.local.dao.RecetaFavoritaDao
import com.example.proyectohealthy.data.local.entity.RecetaFavorita
import kotlinx.coroutines.flow.Flow

class RecetaFavoritaRepository(private val recetaFavoritaDao: RecetaFavoritaDao) {
    fun getAllRecetasFavoritas(): Flow<List<RecetaFavorita>> = recetaFavoritaDao.getAllRecetasFavoritas()

    suspend fun getRecetaFavoritaById(id: Int): RecetaFavorita? = recetaFavoritaDao.getRecetaFavoritaById(id)

    fun getRecetasFavoritasByUserId(userId: Int): Flow<List<RecetaFavorita>> = recetaFavoritaDao.getRecetasFavoritasByUserId(userId)

    suspend fun insertRecetaFavorita(recetaFavorita: RecetaFavorita) = recetaFavoritaDao.insertRecetaFavorita(recetaFavorita)

    suspend fun updateRecetaFavorita(recetaFavorita: RecetaFavorita) = recetaFavoritaDao.updateRecetaFavorita(recetaFavorita)

    suspend fun deleteRecetaFavorita(recetaFavorita: RecetaFavorita) = recetaFavoritaDao.deleteRecetaFavorita(recetaFavorita)

    fun searchRecetasFavoritasByNombre(nombre: String): Flow<List<RecetaFavorita>> = recetaFavoritaDao.searchRecetasFavoritasByNombre(nombre)
}
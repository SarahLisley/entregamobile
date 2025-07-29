package com.example.myapplication.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.myapplication.core.data.database.dao.ReceitaDao
import com.example.myapplication.core.data.database.entity.ReceitaEntity
import kotlinx.coroutines.delay

/**
 * PagingSource para paginação infinita de receitas
 * Carrega receitas do banco local em páginas
 */
class ReceitasPagingSource(
    private val receitaDao: ReceitaDao,
    private val searchQuery: String = ""
) : PagingSource<Int, ReceitaEntity>() {

    companion object {
        const val PAGE_SIZE = 20
        const val INITIAL_LOAD_SIZE = 40
    }

    override fun getRefreshKey(state: PagingState<Int, ReceitaEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReceitaEntity> {
        return try {
            // Simular delay para demonstração (remover em produção)
            delay(300)
            
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val receitas = if (searchQuery.isBlank()) {
                receitaDao.getReceitasPaginated(offset = page * pageSize, limit = pageSize)
            } else {
                receitaDao.searchReceitasPaginated(
                    query = "%$searchQuery%",
                    offset = page * pageSize,
                    limit = pageSize
                )
            }

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (receitas.isEmpty()) null else page + 1

            LoadResult.Page(
                data = receitas,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

/**
 * PagingSource para receitas favoritas
 */
class FavoritosPagingSource(
    private val receitaDao: ReceitaDao,
    private val userId: String
) : PagingSource<Int, ReceitaEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ReceitaEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReceitaEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val receitas = receitaDao.getFavoritosPaginated(
                userId = userId,
                offset = page * pageSize,
                limit = pageSize
            )

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (receitas.isEmpty()) null else page + 1

            LoadResult.Page(
                data = receitas,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

/**
 * PagingSource para receitas por tag
 */
class TagPagingSource(
    private val receitaDao: ReceitaDao,
    private val tag: String
) : PagingSource<Int, ReceitaEntity>() {

    override fun getRefreshKey(state: PagingState<Int, ReceitaEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReceitaEntity> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            
            val receitas = receitaDao.getReceitasPorTagPaginated(
                tag = tag,
                offset = page * pageSize,
                limit = pageSize
            )

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (receitas.isEmpty()) null else page + 1

            LoadResult.Page(
                data = receitas,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
} 
package ru.sicampus.bootcamp2025.ui.mainscreen.allcenters

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.sicampus.bootcamp2025.domain.entities.CenterEntity

class CenterListPagingSource(
    private val request: suspend (pageNum: Int, pageSize: Int) -> Result<List<CenterEntity>>
) : PagingSource<Int, CenterEntity>() {
    override fun getRefreshKey(state: PagingState<Int, CenterEntity>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CenterEntity> {
        val pageNum = params.key ?: 0
        return request.invoke(pageNum, params.loadSize).fold(
            onSuccess = { value ->
                LoadResult.Page(
                    data = value,
                    prevKey = (pageNum - 1).takeIf { it >= 0 },
                    nextKey = (pageNum + 1).takeIf { value.size == params.loadSize }
                )
            },
            onFailure = { error -> LoadResult.Error(error) }
        )
    }
}
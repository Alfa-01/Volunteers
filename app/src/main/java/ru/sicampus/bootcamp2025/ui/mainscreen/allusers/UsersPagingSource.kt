package ru.sicampus.bootcamp2025.ui.mainscreen.allusers

import androidx.paging.PagingSource
import androidx.paging.PagingState
import ru.sicampus.bootcamp2025.domain.entities.ProfileEntity

class UsersPagingSource(
    private val request: suspend (pageNum: Int, pageSize: Int) -> Result<List<ProfileEntity>>
) : PagingSource<Int, ProfileEntity>() {
    override fun getRefreshKey(state: PagingState<Int, ProfileEntity>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProfileEntity> {
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
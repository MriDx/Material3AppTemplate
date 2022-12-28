package com.mridx.androidtemplate.presentation.utils

import androidx.paging.LoadState

fun LoadState.isLoading() = this is LoadState.Loading

fun LoadState.isError() = this is LoadState.Error


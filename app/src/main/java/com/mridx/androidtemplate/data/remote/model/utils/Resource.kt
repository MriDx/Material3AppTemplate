package com.mridx.androidtemplate.data.remote.model.utils

import androidx.annotation.Keep

@Keep
data class Resource<T>(
    var status: Status,
    var data: T? = null,
    var message: String? = null
) {

    companion object {

        fun <T> success(data: T?) = Resource(status = Status.SUCCESS, data = data)

        fun <T> error(message: String?, data: T? = null) =
            Resource(status = Status.FAILED, data = data, message = message)

    }

    fun isSuccess() = status == Status.SUCCESS

    fun isFailed() = status == Status.FAILED


}

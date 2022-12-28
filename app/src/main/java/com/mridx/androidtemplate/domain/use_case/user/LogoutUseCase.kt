package com.mridx.androidtemplate.domain.use_case.user

import com.google.gson.JsonElement
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.domain.repository.user.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator  fun invoke() : Resource<ResponseModel<JsonElement>> {
        return userRepository.logout()
    }

}
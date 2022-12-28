package com.mridx.androidtemplate.domain.use_case.user

import com.mridx.androidtemplate.data.remote.model.auth.AuthResponseModel
import org.json.JSONObject
import com.mridx.androidtemplate.data.remote.model.misc.ResponseModel
import com.mridx.androidtemplate.data.remote.model.utils.Resource
import com.mridx.androidtemplate.domain.repository.user.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(params: JSONObject): Resource<ResponseModel<AuthResponseModel>> {
        return userRepository.login(params)
    }

}
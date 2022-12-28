package com.mridx.androidtemplate.domain.use_case.user

import com.mridx.androidtemplate.data.local.model.user.UserModel
import com.mridx.androidtemplate.domain.repository.user.UserRepository
import javax.inject.Inject

class GetLocalUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke()  : UserModel {
        return userRepository.fetchLocalUser()
    }

}
package ru.bobrov.vyacheslav.chat.gui.dto

import ru.bobrov.vyacheslav.chat.dto.enums.UserRole
import ru.bobrov.vyacheslav.chat.dto.enums.UserStatus
import java.util.*

data class User(
        val userId: UUID,
        val userPic: UUID?,
        val name: String,
        val login: String,
        val status: UserStatus,
        val role: UserRole,
        val created: String,
        val updated: String,
        val jwtToken: String?
)
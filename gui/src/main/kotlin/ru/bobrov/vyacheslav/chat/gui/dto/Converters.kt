package ru.bobrov.vyacheslav.chat.gui.dto

import ru.bobrov.vyacheslav.chat.dto.enums.ChatStatus
import ru.bobrov.vyacheslav.chat.dto.enums.MessageStatus
import ru.bobrov.vyacheslav.chat.dto.enums.UserRole
import ru.bobrov.vyacheslav.chat.dto.enums.UserStatus
import ru.bobrov.vyacheslav.chat.dto.response.*

fun UserRegistrationApiModel.toDto(): User =
        User(
                userId = userId,
                userPic = userPic,
                name = name,
                login = login,
                status = UserStatus.valueOf(status),
                role = UserRole.valueOf(role),
                created = created,
                updated = updated,
                jwtToken = jwtToken
        )

fun UserApiModel.toDto(): User =
        User(
                userId = userId,
                userPic = userPic,
                name = name,
                login = login,
                status = UserStatus.valueOf(status),
                role = UserRole.valueOf(role),
                created = created,
                updated = updated,
                jwtToken = null
        )

fun ChatApiModel.toDto(): Chat =
        Chat(
                chatId = chatId,
                title = title,
                status = ChatStatus.valueOf(status),
                created = created,
                updated = updated,
                creator = creator.toDto()
        )

fun MessageApiModel.toDto(): Message =
        Message(
                messageId = messageId,
                chat = chat.toDto(),
                user = user.toDto(),
                message = message,
                status = MessageStatus.valueOf(status),
                created = created,
                updated = updated
        )

fun MessagesPagingApiModel.toDto(): MessagePage =
        MessagePage(
                messages = messages.map { it.toDto() },
                page = page,
                pageLimit = pageLimit,
                totalItems = totalItems
        )

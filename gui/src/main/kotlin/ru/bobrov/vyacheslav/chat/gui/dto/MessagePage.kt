package ru.bobrov.vyacheslav.chat.gui.dto

data class MessagePage(
        val messages: List<Message>,
        val page: Int,
        val pageLimit: Int,
        val totalItems: Long
)
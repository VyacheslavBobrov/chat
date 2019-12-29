package ru.bobrov.vyacheslav.chat.gui.controllers

import javafx.fxml.FXML
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import net.rgielen.fxweaver.core.FxmlView
import org.springframework.stereotype.Component
import ru.bobrov.vyacheslav.chat.gui.dataproviders.AuthenticationService
import ru.bobrov.vyacheslav.chat.gui.dataproviders.ChatsService
import ru.bobrov.vyacheslav.chat.gui.dataproviders.MessageService
import ru.bobrov.vyacheslav.chat.gui.dataproviders.UsersService
import java.util.*


private const val MESSAGES_PAGE_SIZE = 50

@Component
@FxmlView("ChatScene.fxml")
class ChatController(
        private val authenticationService: AuthenticationService,
        private val usersService: UsersService,
        private val chatsService: ChatsService,
        private val messageService: MessageService
) {
    @FXML
    private lateinit var userInfo: Label

    @FXML
    private lateinit var chatList: ListView<Label>

    @FXML
    private lateinit var chatUsersList: ListView<Label>

    @FXML
    private lateinit var messagesList: ListView<AnchorPane>

    @FXML
    private lateinit var topicLabel: Label

    @FXML
    private lateinit var textArea: TextArea

    @FXML
    private lateinit var buttonSend: Button

    fun init() {
        authenticationService.authenticate("schmul", "123456789")
        val user = authenticationService.user
        userInfo.text = "${user.login}\n${user.name}\n${user.role}\n${user.created}"

        val chats = usersService.getChats()
        chatList.items.clear()
        chatList.items.addAll(
                chats.map { chat ->
                    val item = Label(chat.title)
                    item.setOnMouseClicked { onChatSelect(chat.chatId) }
                    item
                }
        )

        chatUsersList.items.clear()
        messagesList.items.clear()

        topicLabel.text = ""

        buttonSend.isDisable = true

        val scrollBar = getScrollBar(messagesList)
        scrollBar.valueProperty().addListener { _, _, newVal ->
            if (newVal == scrollBar.min && currentMaxMessagePage < maxMessagePage) {
                currentChatId?.let {
                    addMessages(it, ++currentMaxMessagePage)
                }
            }
        }
    }

    private fun addMessages(chatId: UUID, page: Int) {
        val messages = chatsService.getMessages(chatId, page, MESSAGES_PAGE_SIZE)
        messagesList.items.addAll(0,
                messages.messages.map { message ->
                    val label = Label("${message.user.login}\n${message.message}").apply {
                        if (message.user.userId == authenticationService.user.userId)
                            styleClass.add("current-user-message")
                        parent
                    }
                    AnchorPane.setLeftAnchor(label, 10.0)
                    AnchorPane.setRightAnchor(label, 10.0)

                    AnchorPane(label)
                }
        )
        maxMessagePage = messages.pageLimit
    }

    private var currentChatId: UUID? = null
        set(value) {
            buttonSend.isDisable = value == null
            textArea.isEditable = value != null

            currentMaxMessagePage = 0
            maxMessagePage = 0

            field = value
        }

    private var currentMaxMessagePage = 0
    private var maxMessagePage = 0

    private fun onChatSelect(chatId: UUID) {
        currentChatId = chatId

        val users = chatsService.getUsers(chatId)
        chatUsersList.items.clear()
        chatUsersList.items.addAll(users.map { user ->
            Label("${user.login}\n${user.name}\n${user.role}\n${user.created}")
        })
        messagesList.scrollTo(messagesList.items.lastIndex)

        loadMessages(chatId)

        topicLabel.text = chatsService.get(chatId).title
    }

    private fun loadMessages(chatId: UUID) {
        val messages = chatsService.getMessages(chatId, 0, MESSAGES_PAGE_SIZE)
        messagesList.items.clear()
        messagesList.items.addAll(
                messages.messages.map { message ->
                    val label = Label("${message.user.login}\n${message.message}").apply {
                        if (message.user.userId == authenticationService.user.userId)
                            styleClass.add("current-user-message")
                        parent
                    }
                    AnchorPane.setLeftAnchor(label, 10.0)
                    AnchorPane.setRightAnchor(label, 10.0)

                    AnchorPane(label)
                }
        )
        maxMessagePage = messages.pageLimit
    }

    fun onButtonSend() {
        val chatId = currentChatId!!

        if (textArea.text.isBlank())
            return

        messageService.create(chatId, textArea.text)
        textArea.clear()

        loadMessages(chatId)
    }

    fun textAreaOnTyped() {
        buttonSend.isDisable = textArea.text.isBlank()
    }

    private fun getScrollBar(view: ListView<*>): ScrollBar =
            view.lookupAll(".scroll-bar")
                    .first { it is ScrollBar && it.orientation == Orientation.VERTICAL } as ScrollBar

}

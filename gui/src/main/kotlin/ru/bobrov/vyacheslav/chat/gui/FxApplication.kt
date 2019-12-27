package ru.bobrov.vyacheslav.chat.gui

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import net.rgielen.fxweaver.core.FxWeaver
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ConfigurableApplicationContext
import ru.bobrov.vyacheslav.chat.gui.controllers.ChatController
import ru.bobrov.vyacheslav.chat.gui.dataproviders.AuthenticationService
import ru.bobrov.vyacheslav.chat.gui.dto.User

class FxApplication : Application() {
    private var applicationContext: ConfigurableApplicationContext? = null

    override fun start(stage: Stage) {
        val fxWeaver = applicationContext!!.getBean(FxWeaver::class.java)
        val root: Parent = fxWeaver.loadView(ChatController::class.java)
        val scene = Scene(root)
        stage.scene = scene
        println(login())
        stage.show()
    }

    private fun login(): User {
        val authenticationService = applicationContext!!.getBean(AuthenticationService::class.java)
        return authenticationService.authenticate("schmul", "123456789")
    }

    override fun init() {
        val args: Array<String> = parameters.raw.toTypedArray()

        applicationContext = SpringApplicationBuilder()
                .sources(ChatApplication::class.java)
                .run(*args)
    }

    override fun stop() {
        applicationContext?.close()
        Platform.exit()
    }
}

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

class FxApplication : Application() {
    private var applicationContext: ConfigurableApplicationContext? = null

    override fun start(stage: Stage) {
        val fxWeaver: FxWeaver = applicationContext!!.getBean(FxWeaver::class.java)
        val root: Parent = fxWeaver.loadView(ChatController::class.java)
        val scene = Scene(root)
        stage.scene = scene
        stage.show()
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

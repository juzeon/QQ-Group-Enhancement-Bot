package org.example.mirai.plugin

import com.github.juzeon.qgeb.Main
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader

suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    Main.load()
    Main.enable()

//    val bot = MiraiConsole.addBot(123456, "") {
//        fileBasedDeviceInfo()
//    }.alsoLogin()

    MiraiConsole.job.join()
}
package com.github.juzeon.qgeb

import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandExecuteResult
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService.Companion.permit
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info

@ExperimentalCommandDescriptors
object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.juzeon.qgeb",
        name = "QQ-Group-Enhancement-Bot",
        version = "1.0"
    ){
        author("juzeon")
    }
) {
    override fun onEnable() {
        Config.reload()
        Data.reload()
        globalEventChannel().subscribeAlways(MessageEvent::class,coroutineContext) call@{
            val commandExecuteResult=CommandManager.executeCommand(toCommandSender(),message)
            when (commandExecuteResult) {
                is CommandExecuteResult.IllegalArgument -> {
                    commandExecuteResult.exception.message?.let { sender.sendMessage(it) }
                }
                is CommandExecuteResult.ExecutionFailed -> {
                    val owner = commandExecuteResult.command.owner
                    val (logger, printOwner) = when (owner) {
                        is JvmPlugin -> owner.logger to false
                        else -> MiraiConsole.mainLogger to true
                    }
                    logger.warning(
                        "Exception in executing command `$message`" +
                            if (printOwner) ", command owned by $owner" else "",
                        commandExecuteResult.exception
                    )
                }
                is CommandExecuteResult.Success -> {
                    return@call
                }
            }
            MessageEventHandler.handle(this)
        }
        CommandHandler.register()
        AbstractPermitteeId.AnyContact.permit(CommandHandler.permission)
        logger.info { "Loaded QQ-Group-Enhancement-Bot" }
    }

    override fun onDisable() {
        CommandHandler.unregister()
        logger.info { "Disabled QQ-Group-Enhancement-Bot" }
    }
}
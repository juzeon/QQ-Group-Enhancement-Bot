package com.github.juzeon.qgeb

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand

object CommandHandler :CompositeCommand(
    Main,
    "e",
    "e",
    description = "QQ-Group-Enhancement-Bot 指令集"
){
    @SubCommand
    @Description("使用 tinyurl.com 缩短网址")
    suspend fun CommandSender.tinyurl(longUrl:String){
        if (!checkEnable(this,Config.tinyurl)) {
            return
        }
        val receipt=sendMessage("正在调取API，请稍后...")
        sendMessage(Network.getTinyUrl(longUrl))
        receipt?.recall()
    }
    @SubCommand
    @Description("设置配置文件的某个值，只能在控制台调用。"
        +"有哪些值可以设，请查看 config/QQ-Group-Enhancement-Bot/config.yml")
    suspend fun CommandSender.set(key:String,value:String){
        if (this.subject!=null){
            sendMessage("该命令只能通过控制台执行")
            return
        }
        when(key){
            "tinyurl" -> {
                val bool=value.toBoolean()
                Config.tinyurl=bool
                sendMessage("tinyurl 被设置为 $bool")
            }
        }
    }
    suspend inline fun checkEnable(commandSender: CommandSender, isEnable: Boolean):Boolean{
        if(!isEnable){
            commandSender.sendMessage("该命令未启用")
            return false
        }
        return true
    }
}
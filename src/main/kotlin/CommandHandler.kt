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
        val receipt=sendMessage("正在调取API，请稍后...")
        sendMessage(Network.getTinyUrl(longUrl))
        receipt?.recall()
    }

}
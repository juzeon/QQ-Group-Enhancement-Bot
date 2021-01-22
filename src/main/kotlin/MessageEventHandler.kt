package com.github.juzeon.qgeb

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource

object MessageEventHandler {
    suspend fun handle(messageEvent: MessageEvent){
        if(messageEvent.message.contentToString().startsWith("/e")){
            messageEvent.subject.sendMessage(helpText)
        }
        handleWebsitePreview(messageEvent)
    }
    suspend fun handleWebsitePreview(messageEvent: MessageEvent){
        if(!Config.preview || !CoolDown.isCooledDown("preview")) return
        CoolDown.startCoolDown("preview",Config.previewCoolDownSeconds)
        val url= Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
            .find(messageEvent.message.contentToString())
            ?.value ?: return
        when(val preview=Network.getPreview(url)){
            is Preview.PageMeta -> {
                val (title,description,imageUrl)=preview
                val messageChainBuilder=MessageChainBuilder()
                messageChainBuilder.append("[快览] https://outline.com/${url}\n\n[标题] ${title}\n\n[简介] ${description}")
                kotlin.runCatching {
                    val imageStream = imageUrl?.let {
                        Network.getResponseStreamFromUrl(it)
                    }
                    imageStream?.run {
                        val imageMessage = messageEvent.subject.uploadImage(toExternalResource())
                        messageChainBuilder.append("\n\n[图片] ")
                        messageChainBuilder.append(imageMessage)
                    }
                }.onFailure {
                    Main.logger.info("上传Preview图片失败")
                }
                messageEvent.subject.sendMessage(messageChainBuilder.build())
            }
            is Preview.ImageData -> {
                kotlin.runCatching {
                    val imageMessage=messageEvent.subject.uploadImage(preview.dataStream.toExternalResource())
                    messageEvent.subject.sendMessage(imageMessage+"\n$url")
                }.onFailure {
                    Main.logger.info("上传Preview图片失败")
                }
            }
            else -> {
                return
            }
        }
    }
}
package com.github.juzeon.qgeb

import net.mamoe.mirai.event.events.MessageEvent

object MessageEventHandler {
    suspend fun handle(messageEvent: MessageEvent){
        handleWebsitePreview(messageEvent)
    }
    suspend fun handleWebsitePreview(messageEvent: MessageEvent){
        val url=Regex("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)")
            .find(messageEvent.message.contentToString())
            ?.value
        if(url==null){
            return
        }
        val pageMeta=Network.getPageMeta(url)
        if(pageMeta==null){
            messageEvent.subject.sendMessage("网页预览获取失败")
            return
        }
        val (title,description,imageUrl)=pageMeta
        messageEvent.subject.sendMessage("【网址】${url}\n\n【标题】${title}\n\n【简介】${description}")
    }
}
package com.github.juzeon.qgeb

import com.github.juzeon.qgeb.CommandHandler.tinyurl
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource

object CommandHandler : CompositeCommand(
    Main,
    "e",
    "e",
    description = "QQ-Group-Enhancement-Bot 指令集"
) {
    @SubCommand
    @Description("使用 tinyurl.com 缩短网址")
    suspend fun CommandSender.tinyurl(longUrl: String) {
        if (!checkEnable(this, Config.tinyurl)
            || !checkCoolDown(this, "tinyurl")
        ) return
        val receipt = sendMessage("正在调取API，请稍后...")
        sendMessage(Network.getTinyUrl(longUrl))?.run {
            CoolDown.startCoolDown("tinyurl",Config.tinyurlCoolDownSeconds)
        }
        receipt?.recall()
    }

    @SubCommand
    @Description("随机获取ACG图片")
    suspend fun CommandSender.girl(countPassed: Int = 1) {
        if (!checkEnable(this, Config.girl)
            || !checkCoolDown(this, "girl")
        ) return
        var count = countPassed
        if (count > Config.girlCountMax) {
            count = Config.girlCountMax
        }
        val mobileAcgList: List<MobileAcgObj> = kotlin.runCatching {
            Network.getMobileAcgList(count)
        }.getOrElse {
            sendMessage("获取图片失败：" + it.message)
            return
        }
        val jobList = mutableListOf<Job>()
        val messageChainBuilder = MessageChainBuilder()
        mobileAcgList.forEach {
            val job = launch {
                val stream = Network.getResponseStreamFromUrl(
                    if (Config.girlGetViaProxy) it.proxyUrl else it.rawUrl
                ) ?: return@launch
                val image = this@girl.subject?.uploadImage(stream.toExternalResource()) ?: return@launch
                messageChainBuilder.append(image)
            }
            jobList.add(job)
        }
        jobList.forEach {
            it.join()
        }
        sendMessage(messageChainBuilder.build())?.run {
            CoolDown.startCoolDown("girl", Config.girlCoolDownSeconds)
        }
    }

    @SubCommand
    @Description(
        "设置配置文件的某个值，只能在控制台调用。"
            + "有哪些值可以设，请查看 config/QQ-Group-Enhancement-Bot/config.yml"
    )
    suspend fun CommandSender.set(key: String, value: String) {
        if (this.subject != null) {
            sendMessage("该命令只能通过控制台执行")
            return
        }
        when (key) {
            "tinyurl" -> {
                val bool = value.toBoolean()
                Config.tinyurl = bool
                sendMessage("tinyurl 被设置为 $bool")
            }
        }
    }

    suspend inline fun checkEnable(commandSender: CommandSender, isEnable: Boolean): Boolean {
        if (!isEnable) {
            commandSender.sendMessage("该命令未启用")
            return false
        }
        return true
    }

    suspend inline fun checkCoolDown(commandSender: CommandSender, key: String): Boolean {
        val remainingSeconds = CoolDown.getRemainingSeconds(key)
        if (remainingSeconds > 0) {
            commandSender.sendMessage("该命令正在冷却中，时间剩余：${remainingSeconds}秒")
            return false
        }
        return true
    }
}
package com.github.juzeon.qgeb

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config:AutoSavePluginConfig("config") {
    @ValueDescription("是否激活命令 使用 tinyurl.com 缩短网址")
    var tinyurl:Boolean by value(true)
}
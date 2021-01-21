package com.github.juzeon.qgeb

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config:AutoSavePluginConfig("config") {
    @ValueDescription("是否激活命令 使用 tinyurl.com 缩短网址")
    var tinyurl:Boolean by value(true)

    @ValueDescription("使用 tinyurl.com 缩短网址 命令冷却时间")
    var tinyurlCoolDownSeconds:Int by value(10)

    @ValueDescription("是否激活命令 随机获取ACG图片")
    var girl:Boolean by value(true)

    @ValueDescription("随机获取ACG图片 命令单次获取最大图片数量")
    var girlCountMax:Int by value(12)

    @ValueDescription("随机获取ACG图片 命令是否通过代理接口获取（大陆网络运行机器人设为true，否则设为false）")
    var girlGetViaProxy:Boolean by value(true)

    @ValueDescription("随机获取ACG图片 命令冷时间")
    var girlCoolDownSeconds:Int by value(20)
}
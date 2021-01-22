package com.github.juzeon.qgeb

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config:AutoSavePluginConfig("config") {
    @ValueDescription("是否激活链接预览")
    var preview:Boolean by value(true)

    @ValueDescription("链接预览冷却时间")
    var previewCoolDownSeconds:Int by value(5)

    @ValueDescription("是否激活命令 使用 tinyurl.com 缩短网址")
    var tinyurl:Boolean by value(true)

    @ValueDescription("使用 tinyurl.com 缩短网址 命令冷却时间")
    var tinyurlCoolDownSeconds:Int by value(5)

    @ValueDescription("是否激活命令 随机获取ACG图片")
    var girl:Boolean by value(true)

    @ValueDescription("随机获取ACG图片 命令单次获取最大图片数量")
    var girlCountMax:Int by value(12)

    @ValueDescription("随机获取ACG图片 命令冷时间")
    var girlCoolDownSeconds:Int by value(20)
}
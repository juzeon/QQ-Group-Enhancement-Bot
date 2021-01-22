package com.github.juzeon.qgeb

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.Image
import java.io.InputStream

val helpText = """
    |QQ-Group-Enhancement-Bot 命令帮助（/e 命令帮助）
    |/e - 显示本帮助
    |/e tinyurl <url> - 使用 tinyurl.com 缩短网址
    |   eg. /e tinyurl https://github.com/juzeon/QQ-Group-Enhancement-Bot
    |/e girl <count> - 获取二次元图片，count不填默认为1
    |   eg. /e girl 6
""".trimMargin()

sealed class Preview {
    data class PageMeta(
        val title: String?,
        val description: String?,
        val imageUrl: String?
    ) : Preview()

    data class ImageData(val dataStream: InputStream) : Preview()
}

val gson = Gson()
inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)
inline fun <reified T> Gson.fromJson(json: JsonArray) = fromJson<T>(json, object: TypeToken<T>() {}.type)
inline fun <reified T> Gson.fromJson(json: JsonObject) = fromJson<T>(json, object: TypeToken<T>() {}.type)

data class MobileAcgObj(
    @SerializedName("id")
    val id: Int,

    @SerializedName("url")
    val url: String,
)
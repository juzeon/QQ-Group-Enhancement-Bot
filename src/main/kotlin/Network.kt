package com.github.juzeon.qgeb

import com.github.kittinunf.fuel.core.Body
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.HttpException
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okio.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream
import java.time.Duration

object Network {
    val okhttp = OkHttpClient.Builder()
        .addInterceptor(OkInterceptor())
        .connectTimeout(Duration.ofSeconds(15)).build()
    val fuel = FuelManager().apply {
        timeoutInMillisecond = 30 * 1000
        baseHeaders = mapOf(
            Headers.USER_AGENT to "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0"
        )
    }

    fun getTinyUrl(longUrl: String) :String{
        kotlin.runCatching {
            okhttp.newCall(
                Request.Builder().url("https://tinyurl.com/create.php").post(
                    FormBody.Builder()
                        .add("source", "indexpage")
                        .add("url", longUrl)
                        .add("alias", "")
                        .build()
                ).build()
            ).execute()
        }.onSuccess {
            val shortUrl=Regex("<div class=\"indent\"><b>(.*?)</b>").find(it.body?.string()!!)?.value
                ?.substringAfter("<b>")
                ?.substringBefore("</b>")
            return if(shortUrl!=null) shortUrl else "获取短网址失败，请检查网址是否正确"
        }.onFailure {
            return "服务器错误，请稍后再试"
        }
        return ""
    }
    fun getResponseStreamFromUrl(url:String):InputStream?{
        val resp=kotlin.runCatching {
            okhttp.newCall(
                Request.Builder().url(url).get().build()
            ).execute()
        }.onFailure {
            return null
        }.getOrNull()
        return resp?.body?.byteStream()
    }
    fun getResponseStringFromUrl(url:String):String?{
        kotlin.runCatching {
            okhttp.newCall(
                Request.Builder().url(url).get().build()
            ).execute()
        }.onSuccess { response ->
            return response.body?.let {
                it.string()
            }
        }.onFailure {
            return null
        }
        return null
    }
    @Throws(Exception::class)
    fun getMobileAcgList(count:Int):List<MobileAcgObj>{
        val jsonStr=getResponseStringFromUrl("https://api.skyju.cc/mobile-acg/api.php"
                +"?method=json&count=$count") ?: throw IOException("API返回为空")
        val jsonObj=gson.fromJson<JsonObject>(jsonStr)
        return gson.fromJson<List<MobileAcgObj>>(jsonObj.getAsJsonArray("data"))
    }
    fun getPreview(url:String):Preview?{
        val resp=kotlin.runCatching {
            okhttp.newCall(
                Request.Builder().url(url).get().build()
            ).execute()
        }.onFailure {
            return null
        }.getOrNull()
        val contentType=resp?.headers?.get("Content-Type")
        when(contentType){
            "image/jpeg","image/gif","image/png" -> {
                val image: Preview.ImageData?= resp.body?.byteStream()?.let {
                    Preview.ImageData(it)
                }
                return image
            }
            else -> {
                if(contentType?.startsWith("text/html")?:false){
                    val html=resp?.body?.string()
                    val pageMeta: Preview.PageMeta?=html?.let {
                        val doc=Jsoup.parse(it)
                        val imgUrl=doc.select("img[src]")
                            .firstOrNull()
                            ?.attr("abs:src")
                        val title=doc.select("title")
                            .firstOrNull()
                            ?.text()
                        val description=doc.select("meta[name=description]")
                            .firstOrNull()
                            ?.attr("content")
                        Preview.PageMeta(title,description,imgUrl)
                    }
                    return pageMeta
                }else{
                    return null
                }
            }
        }
    }
}

class OkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0")
            .build()
        return chain.proceed(request)
    }
}
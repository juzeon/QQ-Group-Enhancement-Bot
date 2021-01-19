package com.github.juzeon.qgeb

import com.github.kittinunf.fuel.core.Body
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import okhttp3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.Duration

object Network {
    val okhttp = OkHttpClient.Builder()
        .addInterceptor(OkInterceptor())
        .connectTimeout(Duration.ofSeconds(30)).build()
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
    fun getResponseByteFromUrl(url:String){
        kotlin.runCatching {

        }
    }
    fun getResponseStringFromUrl(url:String):String?{
        kotlin.runCatching {
            okhttp.newCall(
                Request.Builder().url(url).get().build()
            ).execute()
        }.onSuccess {
            return it.body!!.string()
        }.onFailure {
            return null
        }
        return null
    }
    fun getPageMeta(url:String):PageMeta?{
        val html=getResponseStringFromUrl(url)
        val pageMeta: PageMeta?=html?.let {
            val doc=Jsoup.parse(it)
            val imgUrl=doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]")
                .firstOrNull()
                ?.attr("src")
            val title=doc.select("title")
                .firstOrNull()
                ?.text()
            val description=doc.select("meta[name=description]")
                .firstOrNull()
                ?.attr("content")
            PageMeta(title,description,imgUrl)
        }
        return pageMeta
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
package per.zsck.simbot.http

import cn.hutool.extra.spring.SpringUtil
import com.fasterxml.jackson.databind.JsonNode
import org.apache.http.HttpHost
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import per.zsck.custom.util.http.HttpBase
import per.zsck.custom.util.jackson.JacksonUtil
import per.zsck.simbot.common.logInfo


/**
 * @author zsck
 * @date   2023/2/15 - 23:51
 */
object HttpProxy: HttpBase(){

    private var requestConfig: RequestConfig


    fun String.toHttpGet(): HttpGet {
        return HttpGet(this)
    }


    override lateinit var httpClient: CloseableHttpClient

    init {
        val port = SpringUtil.getProperty("zsck.proxy.port")

        val proxyAddress = SpringUtil.getProperty("zsck.proxy.addr")

        logInfo("HttpProxy init proxyAddress: {}, port: {}", proxyAddress, port)

//
//        System.setProperty("http.proxySet", "true");
//        // 设置http访问要使用的代理服务器的地址
//        System.setProperty("http.proxyHost", proxyAddress);
//        // 设置http访问要使用的代理服务器的端口
//        System.setProperty("http.proxyPort", port);
//
//        System.setProperty("https.proxySet", "true");
//        // 设置https访问要使用的代理服务器的地址
//        System.setProperty("https.proxyHost", proxyAddress);
//        // 设置https访问要使用的代理服务器的端口
//        System.setProperty("https.proxyPort", port);



        // 使用代理
        val httpHost = HttpHost(proxyAddress, port.toInt())

        requestConfig = RequestConfig.custom()
            .setProxy(httpHost)
            .setConnectTimeout(12000)
            .setSocketTimeout(12000)
            .setConnectionRequestTimeout(12000)
            .build()


        httpClient = HttpClients.custom().setProxy(httpHost).build()

        logInfo("HttpProxy init success")

    }

    fun doGetBytes(url: String): ByteArray{
        val httpGet = url.toHttpGet()
        return EntityUtils.toByteArray(httpClient.execute(httpGet).entity)
    }

    fun doGetStr(url: String): String{
        val httpGet = url.toHttpGet()
        return EntityUtils.toString(httpClient.execute(httpGet).entity)
    }

    fun doGetJson(url: String): JsonNode{

        return JacksonUtil.readTree(doGetStr(url))
    }
}
package per.zsck.simbot.common.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import java.io.IOException
import java.net.URI
import javax.annotation.Resource

/**
 * @author zsck
 * @date   2022/10/31 - 18:32
 */
@Suppress("unused")
abstract class HttpBase{
    private val httpClient = HttpClients.createDefault()


    @Resource
    protected lateinit var objectMapper: ObjectMapper


    @Throws(IOException::class)
    protected fun doGetStr(url: String, header: Array<Header>? = getHeader(), params: Map<String, Any>? = null, isDefault: Boolean = true): String {
        val httpGet = HttpGet(getUri(url, params))
        httpGet.setHeaders(header)
        return doHttpRequestStr(httpGet, isDefault)
    }

    @Throws(IOException::class)
    protected fun doGetBytes(url: String, header: Array<Header>? = getHeader(), params: Map<String, Any>? = null, isDefault: Boolean = true): ByteArray {
        val httpGet = HttpGet( getUri(url, params) )
        httpGet.setHeaders(header)
        return doHttpRequestBytes(httpGet, isDefault)
    }

    @Throws(IOException::class)
    protected fun doPostStr(url: String, entity: HttpEntity? = null, header: Array<Header>? = getHeader(), isDefault: Boolean = true): String {
        val httpPost = HttpPost(url)
        httpPost.setHeaders(header)
        entity?.let { httpPost.entity = it }
        return doHttpRequestStr(httpPost, isDefault)
    }

    @Throws(IOException::class)
    protected fun doGetJson(url: String, header: Array<Header>? = getHeader(), params: Map<String, Any>? = null, isDefault: Boolean = true): JsonNode {
        return objectMapper.readTree(doGetStr(url, header, params, isDefault))
    }
    @Throws(IOException::class)
    protected fun doPostJson(url: String, entity: HttpEntity? = null, header: Array<Header>? = getHeader(), isDefault: Boolean = true): JsonNode {
        return objectMapper.readTree(doPostStr(url, entity, header, isDefault ))
    }

    private fun doHttpRequestStr(httpRequestBase: HttpRequestBase, isDefault: Boolean = true): String{
        try {
            val httpClient : CloseableHttpClient = if (isDefault) this.httpClient else HttpClients.createDefault()

            httpClient.execute(httpRequestBase).use { exec -> return EntityUtils.toString(exec.entity) }
        }finally {//若不使用默认httpClient则自动关闭
            if ( !isDefault ){
                httpClient.close()
            }
        }
    }
    private fun doHttpRequestBytes(httpRequestBase: HttpRequestBase, isDefault: Boolean = true): ByteArray{
        try {
            val httpClient : CloseableHttpClient = if (isDefault) this.httpClient else HttpClients.createDefault()

            httpClient.execute(httpRequestBase).use { exec -> return EntityUtils.toByteArray(exec.entity) }
        }finally {//若不使用默认httpClient则自动关闭
            if ( !isDefault ){
                httpClient.close()
            }
        }
    }

    private fun getUri(url: String, params: Map<String, Any>? = null): URI{
        val uriBuilder = URIBuilder(url)

        params?.let {
            it.forEach { (key, value) -> uriBuilder.addParameter(key, value.toString()) }
        }

        return uriBuilder.build()
    }



    /**
     * 待子类重写
     */
    open fun getHeader(): Array<Header>?{
        return null
    }
}
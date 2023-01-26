package per.zsck.simbot.http.mihoyo.sign.utils

import org.apache.http.Header
import org.apache.http.message.BasicHeader
import per.zsck.simbot.http.mihoyo.sign.HeaderParams
import per.zsck.simbot.http.mihoyo.sign.SignConstant
import java.util.*

/**
 * @author zsck
 * @date   2022/10/31 - 20:51
 */
object HeadersUtil {
    object Builder {
        private val header: MutableMap<String, String> = HashMap()
        fun add(name: String, value: String): Builder {
            header[name] = value
            return this
        }

        fun addAll(headers: Array<Header>): Builder {
            for (h in headers) {
                header[h.name] = h.value
            }
            return this
        }

        fun build(): Array<Header> {
            val list: MutableList<Header> = ArrayList()
            for (key in header.keys) {
                list.add(BasicHeader(key, header[key]))
            }
            return list.toTypedArray()
        }
    }

    fun getBasicHeaders(cookie: String): Array<Header> {
        return Builder.add("Cookie", cookie)
            .add("User-Agent", String.format("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/%s", SignConstant.APP_VERSION))
            .add("Referer", SignConstant.REFERER_URL)
            .add("Accept-Encoding", "gzip, deflate, br")
            .add("x-rpc-channel", "appstore")
            .add("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5")
            .add("accept-encoding", "gzip, deflate")
            .add("accept-encoding", "gzip, deflate")
            .add("x-requested-with", "com.mihoyo.hyperion")
            .add("Host", "api-takumi.mihoyo.com").build()
    }

    fun getHeaders(cookie: String): Array<Header> {
        return Builder.add("x-rpc-device_id",
            UUID.randomUUID().toString().replace("-", "").uppercase(Locale.getDefault())
        )
            .add("Content-Type", "application/json;charset=UTF-8")
            .add("x-rpc-client_type", "2")
            .add("x-rpc-app_version", SignConstant.APP_VERSION)
            .add("DS", HeaderParams.getDS())
            .addAll(getBasicHeaders(cookie)).build()
    }
}
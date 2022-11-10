package per.zsck.simbot.http.mihoyo.sign.utils

import org.apache.http.Header
import org.apache.http.message.BasicHeader
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
        return Builder.build()
    }

    fun getHeaders(cookie: String): Array<Header> {
        return Builder.build()
    }
}
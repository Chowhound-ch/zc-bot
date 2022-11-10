package per.zsck.simbot.http.mihoyo.sign

import org.apache.commons.codec.digest.DigestUtils
import java.util.*

/**
 * @author zsck
 * @date   2022/11/1 - 9:27
 */
object HeaderParams{
    /**
     * 生成随机的字符串作为DS的一部分
     *
     * @return 返回生成的字符串
     */
    fun getRandomStr(): String {
        val random = Random()
        val sb = StringBuilder()
        for (i in 1..6) {
            val constants = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val number = random.nextInt(constants.length)
            val charAt = constants[number]
            sb.append(charAt)
        }
        return sb.toString()
    }

    /**
     * @param n 参数1
     * @param i 参数2
     * @param r 参数3
     * @return 返回创建的DS
     */
    private fun createDS(n: String, i: String, r: String): String {
        val c = DigestUtils.md5Hex("salt=$n&t=$i&r=$r")
        return String.format("%s,%s,%s", i, r, c)
    }

    /**
     * 最终生成的DS
     *
     * @return 返回能加入Headers中的DS
     */
    fun getDS(): String {
        val i = (System.currentTimeMillis() / 1000).toString() + ""
        val r = getRandomStr()
        return createDS("z8DRIUjNDT7IT5IZXvrUAxyupA1peND9", i, r)
    }
}
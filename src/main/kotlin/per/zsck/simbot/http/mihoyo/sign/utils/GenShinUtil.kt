package per.zsck.simbot.http.mihoyo.sign.utils


import per.zsck.simbot.common.utils.HttpBase
import per.zsck.simbot.http.mihoyo.sign.SignConstant
import per.zsck.simbot.http.mihoyo.sign.entity.GenshinInfo

/**
 * @author zsck
 * @date   2022/10/31 - 20:40
 */
object GenShinUtil: HttpBase() {

    fun getSignDataMap(info: GenshinInfo): Map<String, Any> {
        val data: MutableMap<String, Any> = HashMap(3)

        return data.apply {
            this["act_id"] = SignConstant.ACT_ID
            this["region"] = info.serverType.value
            this["uid"] = info.uid
        }
    }

    fun analyzeRet(ret: Int): String? {
        if (ret == SignConstant.RECODE1) {
            return "cookie错误或者你还没有登陆米游社哦，这个cookie是无效的"
        }
        if (ret == SignConstant.RECODE2) {
            return "今天已经签到成功了哦"
        }
        return if (ret == SignConstant.RECODE3) {
            "签到成功(若米游社仍显示未签到是由于验证问题，请手动签到)"
        } else {
            "未知错误，错误码:$ret"
        }
    }

}
package per.zsck.simbot.http.mihoyo.sign

import java.util.*

/**
 * @author zsck
 * @date   2022/10/31 - 18:37
 */
@Suppress("unused")
object SignConstant {
    /**
     * 切勿乱修改
     * 行动id
     */
    const val ACT_ID = "e202009291139501"

    /**
     * 切勿乱修改
     * 米游社版本
     */
    const val APP_VERSION = "2.36.1"

    /**
     * 服务id
     * 切勿乱修改
     * 官服服务id
     */
    const val REGION = "cn_gf01"

    /**
     * B服服务id
     */
    const val REGION2 = "cn_qd01"

    /**
     * Referer网址
     */
    val REFERER_URL = String.format(
        "https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=%s&act_id=%s&utm_source=%s&utm_medium=%s&utm_campaign=%s",
        true,
        ACT_ID,
        "bbs",
        "mys",
        "icon"
    )

    /**
     * 米游社签到链接
     */
    const val SIGN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign"

    /**
     * 米游社主页链接
     */
    const val HOME_URL = "https://bbs-api.mihoyo.com/user/wapi/getUserFullInfo?gids=2"

    const val INFO_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info"

    const val LIST_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/home"


    /**
     * 错误或未登录
     */
    const val RECODE1 = -100

    /**
     * 已经签到过了
     */
    const val RECODE2 = -5003

    /**
     * 签到成功
     */
    const val RECODE3 = 0
}



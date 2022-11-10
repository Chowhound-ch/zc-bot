package per.zsck.simbot.http.mihoyo.sign.enums

import per.zsck.simbot.http.mihoyo.sign.SignConstant

/**
 * @author zsck
 * @date   2022/10/31 - 20:22
 */
enum class ServerType(val value: String) {
    /**
     * 官服
     */
    OFFICIAL(SignConstant.REGION),

    /**
     * B服
     */
    FOREIGN(SignConstant.REGION2);
}
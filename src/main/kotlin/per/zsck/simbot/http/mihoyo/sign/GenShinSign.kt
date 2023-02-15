package per.zsck.simbot.http.mihoyo.sign

import com.fasterxml.jackson.databind.JsonNode
import love.forte.simbot.message.Messages
import love.forte.simbot.message.MessagesBuilder
import love.forte.simbot.resources.URLResource
import org.apache.http.entity.StringEntity
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import per.zsck.custom.util.http.HttpUtil
import per.zsck.custom.util.jackson.JacksonUtil
import per.zsck.simbot.common.logError
import per.zsck.simbot.common.logInfo
import per.zsck.simbot.common.logWarn
import per.zsck.simbot.core.config.EnvironmentConfig
import per.zsck.simbot.http.mihoyo.sign.entity.Award
import per.zsck.simbot.http.mihoyo.sign.entity.GenshinInfo
import per.zsck.simbot.http.mihoyo.sign.entity.SignDetail
import per.zsck.simbot.http.mihoyo.sign.exception.GenShinCookieException
import per.zsck.simbot.http.mihoyo.sign.service.GenshinInfoService
import per.zsck.simbot.http.mihoyo.sign.utils.GenShinUtil
import per.zsck.simbot.http.mihoyo.sign.utils.HeadersUtil
import java.net.URL
import javax.annotation.PostConstruct
import kotlin.streams.toList


/**
 * @author zsck
 * @date   2022/11/1 - 9:28
 */
@Suppress("unused")
@Component
class GenShinSign(var genshinInfoService: GenshinInfoService){
    var awards: List<Award>? = null

    @Value("\${zsck.default-uid}")
    lateinit var defaultUid: String

    @PostConstruct
    fun init(){
        if (EnvironmentConfig.isDev()){
            return
        }

        val genshinInfo = genshinInfoService.getGenshinInfo(defaultUid)

        if (genshinInfo == null ){
            logError("默认账号[uid:{}]不存在，无法更新奖励列表", defaultUid)
        }else{
            if (!updateAwards(genshinInfo)) {
                logError("默认账号[uid:{}]cookie错误，无法更新奖励列表", defaultUid)
            }
        }

    }

    /**
     * 获取cookie对应的Uid,获取对应信息
     */
    fun analyzeCookie(cookie: String): List<GenshinInfo> {
        val result: JsonNode = HttpUtil.doGetJson(
            "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn",
            header = HeadersUtil.getBasicHeaders(cookie)
        )
        val resArr = result.get("data")?.get("list")
        val infoList: MutableList<GenshinInfo> = ArrayList()

        return infoList.apply {
            resArr?.forEach { res ->
                val info = GenshinInfo(
                    res.get("game_uid").asText(), res.get("nickname").asText(), cookie
                )

                logInfo("cookie对应的uid: {} ,昵称: {}", info.uid, info.nickName)
                add(info)
            }
        }
    }

    fun doSignWithAward(uid: String): SignDetail? {
        val info = genshinInfoService.getGenshinInfo(uid)
        doSign(info)
        return info?.let { numberOfSign(it) }
    }
    fun doSignWithAward(info: GenshinInfo): SignDetail? {
        doSign(info)
        return numberOfSign(info)
    }


    /**
     * 签到
     */
    fun doSign(uid: String): String? {
        return doSign(genshinInfoService.getGenshinInfo(uid))
    }

    fun doSign(info: GenshinInfo?): String {
        val builder = StringBuilder()
        return try {
            info?.let { //检查cookie
                checkCookie(it.cookie)
                val data: Map<String, Any> = GenShinUtil.getSignDataMap(it)

                val signResult: JsonNode = HttpUtil.doPostJson(
                    SignConstant.SIGN_URL,
                    StringEntity(JacksonUtil.toJsonString(data)) ,
                    HeadersUtil.getHeaders(it.cookie) ,
                )
                logInfo("签到uid:{} 结果:{}, retcode:{}", it.uid, signResult.get("message"), signResult.get("retcode"))
                builder.append("uid:").append(it.uid).append("\n昵称:").append(it.nickName).append("\n签到结果:")
                    .append(GenShinUtil.analyzeRet(if (signResult.get("ret") == null) signResult.get("retcode").asInt() else signResult.get("ret").asInt()))
            }
            builder.toString()
        } catch (e: GenShinCookieException) {
            e.message!!
        }
    }

    fun numberOfSign(info: GenshinInfo): SignDetail? {
        val signInfoResult: JsonNode = HttpUtil.doGetJson(SignConstant.INFO_URL, HeadersUtil.getHeaders(info.cookie), GenShinUtil.getSignDataMap(info))

        if (signInfoResult.get("message").asText().equals("OK")){
            return signInfoResult.get("data")?.let {
                JacksonUtil.readValue(it.toString(), SignDetail::class.java)
            }
        }
        return null
    }

    private fun updateAwards(info: GenshinInfo): Boolean{

        return info.cookie.let {
            val listInfoResult: JsonNode = HttpUtil.doGetJson(SignConstant.LIST_URL, HeadersUtil.getHeaders(it), GenShinUtil.getSignDataMap(info))

            if (listInfoResult.get("message").asText().equals( "OK" )){

                val awards = listInfoResult.get("data")?.get("awards")?.toList()?.stream()!!
                    .map { award -> JacksonUtil.readValue(award.toString(), Award::class.java)  }.toList()//TODO
                if (awards.isNotEmpty()){
                    this.awards = awards
                    return@let true
                }
            }
            return@let false
        }

    }

    fun checkCookie(cookie: String?) {
        if (cookie == null){
            throw GenShinCookieException("cookie为空")
        }
        val result: JsonNode = HttpUtil.doGetJson(
            "https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=hk4e_cn" ,
            header = HeadersUtil.getBasicHeaders(cookie)
        )
        val retcode: Int = result.get("retcode").asInt()
        if (retcode == SignConstant.RECODE3) {

            logInfo(StringBuilder("cookie正常,ret_code: {}, res: {}, list: ").apply {
                result.get("data").get("list").forEach{
                    this.append("\nuid: ${it["game_uid"].asText()} , 昵称: ${it["nickname"].asText()}")
                }
            }.toString(), retcode, result.get("message").asText())

        } else {
            logWarn("cookie错误,ret_code: {}, res: {}", retcode, result.get("message").asText())
           // throw GenShinCookieException("你还没有登陆米游社哦，这个cookie是无效的")
        }
    }

    /**
     * 获取签到信息对应的simbot消息
     */
    fun getResMsg(info: GenshinInfo, signDetail: SignDetail?): Messages {
        val builder = MessagesBuilder()

        builder.append( "uid: ${info.uid}\n" )
        builder.append( "昵称: ${info.nickName}\n" )

        builder.append( "总签到数: ${signDetail?.totalSignDay ?: "查询失败(cookie可能已失效)"}\n" )
        builder.append( "漏签天数: ${signDetail?.signCntMissed ?: "查询失败(cookie可能已失效)"}\n" )
        builder.append( "当日签到结果: ${signDetail?.isSign ?: "未签到"}" )

        if (signDetail?.isSign == true){
            val award = awards?.get(signDetail.totalSignDay - 1)

            if (award != null){
                builder.append("\n当日奖励: ${award.name} x ${award.number}")
                builder.image(URLResource(URL(award.icon)))
            }else{
                builder.append("\n当日奖励查询失败，请联系管理员")
            }
        }
        return builder.build()
    }


}
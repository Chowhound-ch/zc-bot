package per.zsck.simbot.http.academic.entity

/**
 * @author zsck
 * @date   2022/11/8 - 14:39
 */
data class ClassMap(
    val id: Int?,
    val className: String
){
    constructor(): this(null, "课程")
}

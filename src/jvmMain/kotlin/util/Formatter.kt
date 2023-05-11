package util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//拓展函数，用于格式化显示
fun LocalDateTime.format():String{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return this.format(formatter)
}
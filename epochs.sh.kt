import java.time.LocalDateTime
import java.time.ZoneOffset

fun main() {
    val start = LocalDateTime.of(1970, 1, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
    val end = LocalDateTime.of(2100, 12, 31, 23, 59, 59).toEpochSecond(ZoneOffset.UTC)
    println("START_EPOCH=$start")
    println("END_EPOCH=$end")
}

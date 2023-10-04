package pl.deniotokiari.githubcontributioncalendar

import org.junit.Test

import org.junit.Assert.*
import pl.deniotokiari.githubcontributioncalendar.service.github.type.DateTime
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test() {
        val years = 3
        val now = LocalDateTime.now()
        val fromItems = Array<Pair<LocalDateTime, LocalDateTime>>(years) {
            val from = now.plusYears(-(years - it - 1).toLong()).withDayOfYear(1).with(LocalTime.MIN)
            val to = if (it + 1 == years) {
                now
            } else {
                from.plusYears(1).with(LocalTime.MAX).plusDays(-1)
            }

            from to to
        }

        fromItems.forEach {
            println("${it.first} => ${it.second}")
        }
    }
}
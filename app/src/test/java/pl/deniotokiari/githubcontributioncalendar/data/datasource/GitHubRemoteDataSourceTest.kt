package pl.deniotokiari.githubcontributioncalendar.data.datasource

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import pl.deniotokiari.githubcontributioncalendar.core.failedOrNull
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.network.GitHubService
import java.time.LocalDateTime

class GitHubRemoteDataSourceTest {
    private lateinit var gitHubService: GitHubService
    private lateinit var sut: GitHubRemoteDataSource

    @Before
    fun setUp() {
        gitHubService = mock()
        sut = GitHubRemoteDataSource(gitHubService)
    }

    @Test
    fun `GIVEN contributions for 3 years failed WHEN get contributions for user THEN return error`() = runTest {
        // GIVEN
        whenever(gitHubService.queryUserContribution(any(), any(), any())).thenThrow()

        // WHEN
        val result = sut.getUserContributions(
            "user", listOf(
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0),
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0),
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0)
            )
        )

        // THEN
        assertTrue(result.failedOrNull() != null)
    }

    @Test
    fun `GIVEN contributions for second pair of years failed WHEN get contributions for user THEN return error`() =
        runTest {
            // GIVEN
            val user = "user"
            val secondPair = LocalDateTime.of(4000, 12, 1, 0, 0) to LocalDateTime.of(4000, 12, 1, 0, 0)
            val years = listOf(
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0),
                secondPair,
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0)
            )
            whenever(gitHubService.queryUserContribution(any(), any(), any())).thenAnswer {
                val from = it.arguments[1] as String
                val to = it.arguments[2] as String

                if (from.contains("${secondPair.first.year}") && to.contains("${secondPair.first.year}")) {
                    throw Exception()
                } else {
                    listOf("1", "2", "3", "4")
                }
            }

            // WHEN
            val result = sut.getUserContributions(user, years)

            // THEN
            assertTrue(result.failedOrNull() != null)
        }

    @Test
    fun `GIVEN contributions for all date pairs succeed WHEN get contributions for user THEN return list of contributions`() =
        runTest {
            // GIVEN
            val user = "user"
            val secondPair = LocalDateTime.of(4000, 12, 1, 0, 0) to LocalDateTime.of(4000, 12, 1, 0, 0)
            val years = listOf(
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0),
                secondPair,
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0)
            )
            whenever(gitHubService.queryUserContribution(any(), any(), any())).thenReturn(listOf("1", "2"))

            // WHEN
            val result = sut.getUserContributions(user, years)

            // THEN
            assertTrue(result.successOrNull() != null)
            assertEquals(listOf("1", "2", "1", "2", "1", "2"), result.successOrNull())
        }

    @Test
    fun `GIVEN contributions for any pair of years returns null WHEN get contributions for user THEN return error`() =
        runTest {
            // GIVEN
            val user = "user"
            val secondPair = LocalDateTime.of(4000, 12, 1, 0, 0) to LocalDateTime.of(4000, 12, 1, 0, 0)
            val years = listOf(
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0),
                secondPair,
                LocalDateTime.of(2000, 12, 1, 0, 0) to LocalDateTime.of(2000, 12, 1, 0, 0)
            )
            whenever(gitHubService.queryUserContribution(any(), any(), any())).thenAnswer {
                val from = it.arguments[1] as String
                val to = it.arguments[2] as String

                if (from.contains("${secondPair.first.year}") && to.contains("${secondPair.first.year}")) {
                    null
                } else {
                    listOf("1", "2", "3", "4")
                }
            }

            // WHEN
            val result = sut.getUserContributions(user, years)

            // THEN
            assertTrue(result.failedOrNull() != null)
        }

    @Test
    fun `GIVEN 3 years provided WHEN get date ranges called THEN return list of 3 items`() = runTest {
        // GIVEN
        val years = 3

        // WHEN
        val result = sut.getDateRangesFor(years)

        // THEN
        assertEquals(3, result.successOrNull()?.size)
    }

    @Test
    fun `GIVEN 0 years provided WHEN get date ranges called THEN return empty list`() = runTest {
        // GIVEN
        val years = 0

        // WHEN
        val result = sut.getDateRangesFor(years)

        // THEN
        assertTrue(result.successOrNull()?.isEmpty() == true)
    }

    @Test
    fun `GIVEN -1 years provided WHEN get date ranges called THEN return empty list`() = runTest {
        // GIVEN
        val years = 0

        // WHEN
        val result = sut.getDateRangesFor(years)

        // THEN
        assertTrue(result.successOrNull()?.isEmpty() == true)
    }
}
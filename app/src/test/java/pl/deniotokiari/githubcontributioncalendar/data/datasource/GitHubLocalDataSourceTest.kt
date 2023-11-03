package pl.deniotokiari.githubcontributioncalendar.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import pl.deniotokiari.githubcontributioncalendar.core.failedOrNull
import pl.deniotokiari.githubcontributioncalendar.core.successOrNull
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions

class GitHubLocalDataSourceTest {
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var sut: GitHubLocalDataSource

    @Before
    fun setUp() {
        dataStore = mock()
        sut = GitHubLocalDataSource(dataStore)
    }

    @Test
    fun `GIVEN dataStore fails to add contributions WHEN add contributions THEN return error`() = runTest {
        // GIVEN
        whenever(dataStore.edit(any())).thenThrow()

        // WHEN
        val result = sut.addContributions("user", Contributions(mock()))

        // THEN
        assertNotNull(result.failedOrNull())
    }

    @Test
    fun `GIVEN dataStore fails to remove contributions WHEN add contributions THEN return error`() = runTest {
        // GIVEN
        whenever(dataStore.edit(any())).thenThrow()

        // WHEN
        val result = sut.removeContributions("user")

        // THEN
        assertNotNull(result.failedOrNull())
    }

    @Test
    fun `GIVEN dataStore success to add contributions WHEN add contributions THEN return success`() = runTest {
        // GIVEN
        val prefs: MutablePreferences = mock()
        whenever(prefs.set<String>(any(), any())).thenAnswer { }
        whenever(dataStore.edit(any())).thenReturn(prefs)

        // WHEN
        val result = sut.addContributions("user", Contributions(mock()))

        // THEN
        assertNotNull(result.successOrNull())
    }

    @Test
    fun `GIVEN dataStore success to remove contributions WHEN add contributions THEN return success`() = runTest {
        // GIVEN
        val prefs: MutablePreferences = mock()
        whenever(prefs.remove<String>(any())).thenReturn("")
        whenever(dataStore.edit(any())).thenReturn(prefs)

        // WHEN
        val result = sut.removeContributions("user")

        // THEN
        assertNotNull(result.successOrNull())
    }
}
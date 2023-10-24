package pl.deniotokiari.githubcontributioncalendar.prefs

class AppConfigurationRepository(
    private val appConfigurationRemoteDataSource: AppConfigurationRemoteDataSource
) {
    fun getRepeatInterval(): Long = appConfigurationRemoteDataSource.getRepeatInterval()

    fun getSupportEmail(): String = appConfigurationRemoteDataSource.getSupportEmail()

    fun getYears(): Int = appConfigurationRemoteDataSource.getYears()
}
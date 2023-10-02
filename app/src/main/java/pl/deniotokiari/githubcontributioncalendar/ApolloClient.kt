package pl.deniotokiari.githubcontributioncalendar

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response

val apolloClient = ApolloClient.Builder()
    .serverUrl(BuildConfig.GITHUB_URL)
    .okHttpClient(
        OkHttpClient
            .Builder()
            .addInterceptor(AuthorizationInterceptor())
            .build()
    )
    .build()

private class AuthorizationInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .apply {
                addHeader("Authorization", "Bearer ${BuildConfig.GITHUB_TOKEN}")
            }
            .build()

        return chain.proceed(request)
    }
}
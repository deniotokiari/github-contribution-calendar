package pl.deniotokiari.githubcontributioncalendar.core

interface UseCase<in A, out B> {
    suspend operator fun invoke(params: A): B
}
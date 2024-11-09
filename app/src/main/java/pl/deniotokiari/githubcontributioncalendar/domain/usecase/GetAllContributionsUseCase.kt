package pl.deniotokiari.githubcontributioncalendar.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.deniotokiari.githubcontributioncalendar.core.FlowUseCase
import pl.deniotokiari.githubcontributioncalendar.core.Result
import pl.deniotokiari.githubcontributioncalendar.core.failed
import pl.deniotokiari.githubcontributioncalendar.core.fold
import pl.deniotokiari.githubcontributioncalendar.core.success
import pl.deniotokiari.githubcontributioncalendar.data.model.Contributions
import pl.deniotokiari.githubcontributioncalendar.data.model.UserName
import pl.deniotokiari.githubcontributioncalendar.data.repository.ContributionsRepository
import pl.deniotokiari.githubcontributioncalendar.domain.model.DomainError

class GetAllContributionsUseCase(
    private val contributionsRepository: ContributionsRepository
) :
    FlowUseCase<Unit, Result<List<Pair<UserName, Contributions>>, DomainError>> {
    override fun invoke(params: Unit): Flow<Result<List<Pair<UserName, Contributions>>, DomainError>> =
        contributionsRepository
            .allContributions()
            .map { result ->
                result.fold(
                    success = { items ->
                        items.map { item ->
                            Pair(
                                first = UserName(item.first),
                                second = item.second,
                            )
                        }.success()
                    },
                    failed = { DomainError(it.throwable).failed() },
                )
            }
}

package pl.deniotokiari.githubcontributioncalendar.widget.usecase

import android.content.Context
import androidx.glance.appwidget.updateAll
import pl.deniotokiari.githubcontributioncalendar.core.UseCase
import pl.deniotokiari.githubcontributioncalendar.widget.AppWidget

class UpdateAllWidgetsUseCase(private val context: Context) : UseCase<Unit, Unit> {
    override suspend fun invoke(params: Unit) {
        AppWidget().updateAll(context)
    }
}
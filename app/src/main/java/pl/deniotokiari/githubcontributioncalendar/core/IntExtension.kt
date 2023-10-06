package pl.deniotokiari.githubcontributioncalendar.core

import android.content.res.Resources

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
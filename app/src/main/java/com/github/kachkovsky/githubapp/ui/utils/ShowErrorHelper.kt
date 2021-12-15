package com.github.kachkovsky.githubapp.ui.utils

class ShowErrorHelper {

    private var lastErrorTime: Long = 0
    fun needShowError(time: Long): Boolean {
        if (time != lastErrorTime) {
            lastErrorTime = time
            return true

        }
        return false
    }
}
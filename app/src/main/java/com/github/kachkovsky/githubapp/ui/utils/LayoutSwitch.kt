package com.github.kachkovsky.githubapp.ui.utils

import android.view.View


class LayoutSwitch<T> @SafeVarargs constructor(private vararg val layoutObjects: LayoutObject<T>) {
    private var currentState: T? = null

    fun showLayout(obj: T) {
        if (obj == currentState) {
            return
        }
        var doVisible: LayoutObject<T>? = null
        for (layoutObject in layoutObjects) {
            if (layoutObject.state == obj) {
                doVisible = layoutObject
            } else {
                layoutObject.change(false, obj)
            }
        }
        doVisible?.change(true, obj)
        currentState = obj
    }

    val isStateDefined: Boolean
        get() = currentState != null

    interface StateChangedInterface<T> {
        fun onStateChanged(currentStateVisible: Boolean, visibleState: T)
    }

    class LayoutObject<T> {
        private val view: View?
        val state: T
        private val stateChangedInterface: StateChangedInterface<T>?

        constructor(state: T, view: View?) {
            this.view = view
            this.state = state
            stateChangedInterface = null
        }

        constructor(state: T, stateChangedInterface: StateChangedInterface<T>?) {
            view = null
            this.state = state
            this.stateChangedInterface = stateChangedInterface
        }

        fun change(value: Boolean, newState: T) {
            if (stateChangedInterface != null) {
                stateChangedInterface.onStateChanged(value, newState)
            } else {
                view!!.visibility = if (value) View.VISIBLE else View.GONE
            }
        }
    }

}

package uk.co.goingproprogramming.tbp.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class ViewModelBase<S>(
    initialState: S,
) : ViewModel() {
    protected var localState: S = initialState
        set(value) {
            field = value
            notifyUpdate()
        }
    private val _state = MutableLiveData(localState)
    val state: LiveData<S> = _state

    private fun notifyUpdate() {
        _state.value = localState
    }
}
package com.example.kartsho

import android.content.Context

private lateinit var _appContext: Context

var appContext: Context
    get() = _appContext
    set(value) { _appContext = value }

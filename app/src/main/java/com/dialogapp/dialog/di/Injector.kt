package com.dialogapp.dialog.di

import com.dialogapp.dialog.MicroblogApp

class Injector private constructor() {
    companion object {
        fun get(): AppComponent =
                MicroblogApp.get().appComponent
    }
}
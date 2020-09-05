package com.numerad.numeriq.di

import com.google.gson.GsonBuilder
import org.koin.dsl.module

val mainModule = module {
    single { GsonBuilder().setLenient().create() }
}

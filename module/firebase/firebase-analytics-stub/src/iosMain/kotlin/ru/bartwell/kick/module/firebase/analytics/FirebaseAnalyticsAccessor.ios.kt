package ru.bartwell.kick.module.firebase.analytics

import platform.Foundation.NSDictionary

@Suppress("UnusedParameter", "EmptyFunctionBlock")
public fun FirebaseAnalyticsAccessor.logEvent(name: String, params: NSDictionary?) {}

@Suppress("UnusedParameter", "EmptyFunctionBlock")
public fun FirebaseAnalyticsAccessor.logEvent(name: String, params: Map<Any?, *>?) {}

@Suppress("UnusedParameter", "EmptyFunctionBlock")
public fun FirebaseAnalyticsAccessor.setUserId(id: String?) {}

@Suppress("UnusedParameter", "EmptyFunctionBlock")
public fun FirebaseAnalyticsAccessor.setUserProperty(name: String, value: String) {}

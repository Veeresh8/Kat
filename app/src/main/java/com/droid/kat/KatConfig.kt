package com.droid.kat

import javax.inject.Inject
import javax.inject.Singleton

class KatConfig @Inject constructor() {
    val pageLimit = 20
    var currentPage = 1
}
package com.example.qrscanner

fun String.isNumeric(): Boolean = this.all { it in '0'..'9' }
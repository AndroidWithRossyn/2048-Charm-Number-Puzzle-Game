package com.rossyn.blocktiles.game2048.utils

import kotlinx.coroutines.delay

const val FLASH_TIME = 50L
const val BLINK_TIME = 100L
const val HICCUP = 150L
const val MINI_BREAK = 300L
const val JUST_A_MOMENT = 500L
const val SHORT_LAUGH = 500L
const val HMMM = 800L
const val JUST_SECOND = 1000L
const val LET_ME_THINK = 1500L
const val BUFFERING = 2000L
const val TAKE_A_BREATH = 2500L
const val WAIT_WHAT = 3000L
const val BORING_AD = 5000L

const val MAGGI_TIME = 1000L*60*2
const val COFFEE_BREAK = 1000L*60*5
const val FOCUS_RESET = 1000L*60*10
const val POWER_NAP = 1000L*60*15
const val LUNCH_TIME = 1000L*60*30

suspend fun blinkTime() = delay(100L)
suspend fun hiccup() = delay(150L)
suspend fun miniBreak() = delay(300L)
suspend fun quickSwipe() = delay(600L)
suspend fun shortLaugh() = delay(700L)
suspend fun hmmm() = delay(800L)
suspend fun justAMoment() = delay(500L)
suspend fun justSecond() = delay(1000L)
suspend fun letMeThink() = delay(1500L)
suspend fun buffering() = delay(2000L)
suspend fun takeABreath() = delay(2500L)
suspend fun waitWhat() = delay(3000L)
suspend fun boringAd() = delay(5000L)

suspend fun maggiTime() = delay(1000*60*2)
suspend fun coffeeBreak() = delay(1000*60*5)
suspend fun powerNap() = delay(1000*60*15)
suspend fun lunchTime() = delay(1000*60*30)
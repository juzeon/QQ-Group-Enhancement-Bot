package com.github.juzeon.qgeb

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.utils.info

object CoolDown {
    val coolDownList = mutableMapOf<String, Int>()
    fun startCoolDown(key: String, seconds: Int) {
        if(seconds==0) return
        coolDownList[key] = seconds
        GlobalScope.launch {
            while (true) {
                delay(1000)
                coolDownList[key] = coolDownList[key]!! - 1
                if (coolDownList[key]!! <= 0) {
                    Main.logger.info("$key 冷却完成")
                    return@launch
                }
            }
        }
    }

    fun getRemainingSeconds(key: String): Int {
        return coolDownList.get(key) ?: return 0
    }

    fun isCooledDown(key: String): Boolean {
        return getRemainingSeconds(key) == 0
    }
}
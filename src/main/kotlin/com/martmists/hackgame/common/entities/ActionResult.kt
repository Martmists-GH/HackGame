package com.martmists.hackgame.common.entities

enum class ActionResult {
    FAIL,
    PASS;

    companion object {
        fun <T> conditionally(items: List<T>, block: (T) -> ActionResult): ActionResult {
            var state = PASS
            for (item in items) {
                state = block(item)
                if (state == FAIL) {
                    break
                }
            }
            return state
        }

        fun <T> all(items: List<T>, block: (T) -> ActionResult): ActionResult {
            var state = PASS
            for (item in items) {
                val res = block(item)
                if (res == FAIL) {
                    state = res
                }
            }
            return state
        }
    }
}

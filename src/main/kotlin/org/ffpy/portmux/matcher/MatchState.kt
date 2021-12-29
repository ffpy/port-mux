package org.ffpy.portmux.matcher

/**
 * 匹配状态
 */
enum class MatchState {
    /** 匹配 */
    MATCH,

    /** 不匹配 */
    NOT_MATCH,

    /** 部分匹配 */
    MAYBE
}
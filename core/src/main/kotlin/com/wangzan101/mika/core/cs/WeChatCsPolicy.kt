package com.wangzan101.mika.core.cs

/**
 * 微信客服分类草稿（v1 不接入发送链路，仅作逻辑说明）。
 *
 * 目的：把一条客户消息分成两类——
 *  - DRAFT：可按 FAQ 起草，仍需用户说“发”才发送；
 *  - ESCALATE：停下转人工，不直接给结论、不直接发送。
 *
 * 注意：这段代码本身不是审批拦截。Mika 原项目的审批层目前只拦花钱那一步，
 * 微信发消息默认不拦，所以 v1 的“人工确认”靠 Skill 流程保证；
 * 代码层硬拦截是 v2 工作，需要改审批层并真机验证。
 */
enum class CsDecision {
    DRAFT,
    ESCALATE,
}

object WeChatCsPolicy {

    private val escalateKeywords = listOf(
        // 售后与承诺
        "退款", "退货", "换货", "补发", "赔偿", "优惠", "便宜点", "改价", "改单", "改地址",
        // 投诉与升级
        "投诉", "差评", "消协", "12315", "律师", "人工", "转人工", "找你们老板",
        // 账号、付款与安全
        "付款", "付钱", "验证码", "密码", "银行卡", "转账",
        // 英文同义（少量兜底，中文为主）
        "refund", "complaint",
    )

    fun classify(message: String): CsDecision {
        val text = message.trim()
        if (text.isEmpty()) return CsDecision.ESCALATE
        return if (escalateKeywords.any { text.contains(it) }) {
            CsDecision.ESCALATE
        } else {
            CsDecision.DRAFT
        }
    }

    /** 脱敏：汇总时不要原样复述手机号、地址、完整订单号。 */
    fun redactForSummary(text: String): String {
        var out = text
        // 手机号：保留前 3 后 4，其余打码
        out = out.replace(Regex("1[3-9]\\d{9}")) { m ->
            val s = m.value
            s.substring(0, 3) + "****" + s.substring(7)
        }
        return out
    }
}

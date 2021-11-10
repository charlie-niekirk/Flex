package me.cniekirk.flex.data.remote.model.rules


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rules(
    val rules: List<Rule>?,
    @Json(name = "site_rules")
    val siteRules: List<String>?,
    @Json(name = "site_rules_flow")
    val siteRulesFlow: List<SiteRulesFlow>?
)
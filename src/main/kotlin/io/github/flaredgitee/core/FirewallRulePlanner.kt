/*
 * Copyright 2025 flaredgitee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.flaredgitee.core

import io.github.flaredgitee.core.models.FirewallRule

/** Computes whether firewall rules need to change and returns the desired ruleset. */
object FirewallRulePlanner {
    /**
     * Compares the current rules with the latest IP and returns a new list when an update is required.
     */
    fun calculateDesiredRules(
        currentRules: List<FirewallRule>,
        currentIp: String,
        appName: String
    ): List<FirewallRule>? {
        val isV6 = currentIp.contains(":")
        val ipExists =
            currentRules.any { rule ->
                val ruleIp = if (isV6) rule.ipv6Cidr else rule.ipv4Cidr
                ruleIp == currentIp
            }

        if (ipExists) {
            return null
        }

        val keptRules = currentRules.filter { it.description != appName }
        val newRule =
            FirewallRule(
                protocol = "ALL",
                port = "ALL",
                action = "ACCEPT",
                description = appName,
                ipv6Cidr = if (isV6) currentIp else null,
                ipv4Cidr = if (!isV6) currentIp else null
            )

        return keptRules + newRule
    }
}

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

import io.github.flaredgitee.core.models.AppConfig
import io.github.flaredgitee.core.spi.FirewallProvider
import io.github.flaredgitee.core.spi.IpResolver

/** Coordinates IP resolution and firewall updates for a single CLI invocation. */
class FirewallOrchestrator(
    private val firewallProvider: FirewallProvider,
    private val ipResolver: IpResolver,
) {
    /**
     * Resolves the current IP, reads remote firewall rules, plans updates, and applies them if needed.
     */
    fun runByConfig(config: AppConfig): Result<FirewallOrchestrator> = runCatching {

        val ip = config.ipCommand
            .also { println("Try to get IP with: $it") }
            .let(ipResolver::resolve)
            .onSuccess { println("Got current IP successfully: $it") }
            .onFailure { println("Failed to get IP: ${it.message}") }
            .getOrElse { throw it }

        val currentRules = firewallProvider.fetchRules(config.instanceId)
            .onFailure { println("Failed to fetch rules: ${it.message}") }
            .getOrElse { throw it }

        val newRules = calculateDesiredRules(currentRules, ip, config.ruleName)

        newRules?.let { desiredRules ->
            println("Record new IP $ip to server firewall")
            firewallProvider.updateRules(config.instanceId, desiredRules)
                .onSuccess {
                    println("Record success")
                    println(it)
                }
                .onFailure {
                    println("Failed to update rules: ${it.message}")
                    throw it
                }
        } ?: run {
            println("No firewall update needed.")
        }

        this
    }
}

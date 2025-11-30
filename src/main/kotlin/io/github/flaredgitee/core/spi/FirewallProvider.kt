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

package io.github.flaredgitee.core.spi

import io.github.flaredgitee.core.models.FirewallRule

/** Abstraction over any cloud vendor firewall API. */
interface FirewallProvider {
    /** Fetches the current firewall rules for the specified instance. */
    fun fetchRules(instanceId: String): Result<List<FirewallRule>>

    /** Submits the desired firewall ruleset to the remote instance. */
    fun updateRules(instanceId: String, rules: List<FirewallRule>): Result<String>
}

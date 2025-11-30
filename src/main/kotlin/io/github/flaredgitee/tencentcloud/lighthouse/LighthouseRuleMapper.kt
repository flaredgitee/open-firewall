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

package io.github.flaredgitee.tencentcloud.lighthouse

import com.tencentcloudapi.lighthouse.v20200324.models.FirewallRuleInfo
import io.github.flaredgitee.core.models.FirewallRule
import com.tencentcloudapi.lighthouse.v20200324.models.FirewallRule as SdkFirewallRule

/** Converts Lighthouse SDK rule info into the internal [FirewallRule]. */
fun FirewallRuleInfo.toCommonModel(): FirewallRule =
    FirewallRule(
        protocol = this.protocol,
        port = this.port,
        action = this.action,
        description = this.firewallRuleDescription,
        ipv4Cidr = this.cidrBlock,
        ipv6Cidr = this.ipv6CidrBlock
    )

/** Converts the internal [FirewallRule] into a Lighthouse SDK rule request object. */
fun FirewallRule.toSdkModel(): SdkFirewallRule = let { commonRule ->
    SdkFirewallRule().apply {
        protocol = commonRule.protocol
        port = commonRule.port
        action = commonRule.action
        firewallRuleDescription = commonRule.description
        commonRule.ipv4Cidr?.let { cidrBlock = it }
        commonRule.ipv6Cidr?.let { ipv6CidrBlock = it }
    }
}

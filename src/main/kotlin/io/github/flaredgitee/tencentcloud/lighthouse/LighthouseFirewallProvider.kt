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

import com.tencentcloudapi.common.profile.ClientProfile
import com.tencentcloudapi.common.profile.HttpProfile
import com.tencentcloudapi.common.provider.ProfileCredentialsProvider
import com.tencentcloudapi.lighthouse.v20200324.LighthouseClient
import com.tencentcloudapi.lighthouse.v20200324.models.DescribeFirewallRulesRequest
import com.tencentcloudapi.lighthouse.v20200324.models.ModifyFirewallRulesRequest
import io.github.flaredgitee.core.models.FirewallRule
import io.github.flaredgitee.core.spi.FirewallProvider

/** Tencent Cloud Lighthouse implementation of [FirewallProvider]. */
class LighthouseFirewallProvider(
    private val region: String
) : FirewallProvider {

    private val client: LighthouseClient by lazy { createClient() }

    /** Retrieves existing Tencent Cloud Lighthouse firewall rules for the given instance. */
    override fun fetchRules(instanceId: String): Result<List<FirewallRule>> = runCatching {
        val req = DescribeFirewallRulesRequest().apply { this.instanceId = instanceId }
        val resp = client.DescribeFirewallRules(req)
        resp.firewallRuleSet.map { it.toCommonModel() }
    }

    /** Applies the supplied rules to the Lighthouse instance and returns the API response JSON. */
    override fun updateRules(instanceId: String, rules: List<FirewallRule>): Result<String> = runCatching {
        val req =
            ModifyFirewallRulesRequest().apply {
                this.instanceId = instanceId
                this.firewallRules = rules.map { it.toSdkModel() }.toTypedArray()
            }
        val resp = client.ModifyFirewallRules(req)
        ModifyFirewallRulesRequest.toJsonString(resp)
    }

    /** Creates the Lighthouse client using default profile credentials and region settings. */
    private fun createClient(): LighthouseClient {
        val cred = ProfileCredentialsProvider().credentials
        val httpProfile = HttpProfile().apply { endpoint = "lighthouse.tencentcloudapi.com" }
        val clientProfile = ClientProfile().apply { this.httpProfile = httpProfile }
        return LighthouseClient(cred, region, clientProfile)
    }
}

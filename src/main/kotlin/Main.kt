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

import io.github.flaredgitee.core.FirewallOrchestrator
import io.github.flaredgitee.core.ShellIpResolver
import io.github.flaredgitee.core.loadConfig
import io.github.flaredgitee.tencentcloud.lighthouse.LighthouseFirewallProvider
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import kotlin.system.exitProcess

/** CLI entry point that parses arguments, loads configuration, and runs the orchestrator once. */
fun main(args: Array<String>) {
    val parser = ArgParser("open-firewall")
    val configPath by parser.option(
        ArgType.String,
        shortName = "c",
        fullName = "config",
        description = """
            TOML config file path. File must define:
              - instanceId: target Lighthouse instance ID
              - ruleName: unique marker inserted into firewall rules
              - ipCommand: shell command printing current public IP
              - region: Tencent Cloud region, e.g. ap-beijing
            Example:
              instanceId = "lh-123"
              ruleName = "openfirewall"
              ipCommand = "curl -s https://api.ipify.org"
              region = "ap-beijing"
        """.trimIndent()
    )
        .required()
    parser.parse(args)

    val config = loadConfig(configPath) ?: exitProcess(1)

    val firewallProvider = LighthouseFirewallProvider(config.region)
    val ipResolver = ShellIpResolver()
    val orchestrator = FirewallOrchestrator(firewallProvider, ipResolver)

    println("Starting OpenFirewall for instance ${config.instanceId}...")

    val result = orchestrator.runByConfig(config)
        .onFailure { e ->
            println("Unexpected error during execution: ${e.message}")
            println(e.stackTraceToString())
        }

    if (result.isFailure) {
        exitProcess(1)
    }
}

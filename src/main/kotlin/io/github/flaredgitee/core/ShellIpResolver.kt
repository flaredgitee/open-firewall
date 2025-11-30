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

import io.github.flaredgitee.core.spi.IpResolver
import java.util.concurrent.TimeUnit

/** Executes a user-supplied shell command to determine the current public IP address. */
class ShellIpResolver : IpResolver {
    /** Executes the shell command and validates stdout as the current IP address. */
    override fun resolve(command: String): Result<String> = runCatching {
        val parts = command.trim().split("\\s+".toRegex())
        val process = ProcessBuilder(parts).redirectErrorStream(true).start()

        val output = process.inputStream.bufferedReader().use { it.readText() }.trim()
        val exited = process.waitFor(10, TimeUnit.SECONDS)

        if (!exited) {
            process.destroy()
            throw RuntimeException("Command timed out")
        }
        if (process.exitValue() != 0) {
            throw RuntimeException("Command failed (exit ${process.exitValue()}): $output")
        }
        if (output.isBlank()) {
            throw RuntimeException("Command returned empty output")
        }
        output
    }
}

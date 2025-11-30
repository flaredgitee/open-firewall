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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.toml.TomlFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.flaredgitee.core.models.AppConfig
import java.io.File

private val mapper = ObjectMapper(TomlFactory()).registerKotlinModule()

/**
 * Loads an [AppConfig] from the provided TOML path, returning null if the file is missing or invalid.
 */
fun loadConfig(configPath: String): AppConfig? {
    val configFile = File(configPath)
    if (!configFile.exists()) {
        println("Config file not found: ${configFile.absolutePath}")
        return null
    }

    return try {
        mapper.readValue(configFile, AppConfig::class.java)
    } catch (e: Exception) {
        println("Failed to parse config: ${e.message}")
        null
    }
}

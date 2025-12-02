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

plugins {
    kotlin("jvm") version "2.2.20"
    application
}

group = "io.github.flaredgitee"
version = "0.1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.tencentcloudapi:tencentcloud-sdk-java-common:3.1.1365")
    implementation("com.tencentcloudapi:tencentcloud-sdk-java-lighthouse:3.1.1365")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.15.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.6")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

application {
    mainClass.set("MainKt")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith(".jar") }
            .map { zipTree(it) }
    })
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

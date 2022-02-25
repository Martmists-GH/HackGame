import org.gradle.internal.os.OperatingSystem
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("com.github.ben-manes.versions") version "0.42.0"
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
}

group = "com.martmists"
version = "1.0.0"

val common by configurations.creating
val commonRuntime by configurations.creating
val client by configurations.creating
val clientRuntime by configurations.creating
val server by configurations.creating
val serverRuntime by configurations.creating

configurations["implementation"].apply {
    extendsFrom(common)
    extendsFrom(client)
    extendsFrom(server)
}
configurations["runtimeOnly"].apply {
    extendsFrom(commonRuntime)
    extendsFrom(clientRuntime)
    extendsFrom(serverRuntime)
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.martmists.com/releases/")
    maven("https://maven.fabricmc.net/")
    maven("https://kotlin.bintray.com/kotlinx/")
    maven("https://repo1.maven.org/maven2")
    maven("https://jitpack.io")
}

dependencies {
    // === COMMON ===
    // Kotlin
    common(kotlin("stdlib"))

    // Networking + Serialization
    common("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.2")
    common("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")
    common("io.ktor:ktor-network:1.6.7")

    // Scripting
    common("org.jetbrains.kotlin:kotlin-scripting-common")
    common("org.jetbrains.kotlin:kotlin-scripting-jvm")
    common("org.jetbrains.kotlin:kotlin-scripting-jvm-host")

    // Coroutines
    common("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")

    // Argument Parsing
    common("org.jetbrains.kotlinx:kotlinx-cli-jvm:0.3.4")

    // Command parsing
    common("com.martmists:command_parser:1.2.5")

    // Launcher
    common("net.fabricmc:fabric-loader:0.13.2")

    // Logging
    commonRuntime("ch.qos.logback:logback-classic:1.2.10")

    // Configs
    for (module in listOf("core:jackson-core", "core:jackson-databind", "core:jackson-annotations", "module:jackson-module-kotlin", "dataformat:jackson-dataformat-yaml")) {
        common("com.fasterxml.jackson.$module:2.13.1")
    }
    common("org.yaml:snakeyaml:1.30")

    // Manifest parsing
    common("com.jcabi:jcabi-manifests:1.1")

    // Runtime dependencies
    for (module in listOf("asm", "asm-analysis", "asm-commons", "asm-tree", "asm-util")) {
        commonRuntime("org.ow2.asm:$module:9.2")
    }
    commonRuntime("net.fabricmc:access-widener:2.1.0")
    commonRuntime("net.fabricmc:sponge-mixin:0.11.2+mixin.0.8.5")

    // === SERVER ONLY ===
    // Database + drivers
    for (module in listOf("core", "jdbc", "java-time")) {
        server("org.jetbrains.exposed:exposed-$module:0.37.3")
    }
    serverRuntime("org.postgresql:postgresql:42.3.3")
    serverRuntime("org.xerial:sqlite-jdbc:3.36.0.3")

    // Password hashing
    server("de.mkammerer:argon2-jvm-nolibs:2.11")


    // === CLIENT ONLY ===
    // Lwjgl
    val lwjglNatives: String
    val skijaNatives: String
    when(OperatingSystem.current()) {
        OperatingSystem.LINUX -> {
            val osArch = System.getProperty("os.arch")
            lwjglNatives = if (osArch.startsWith("arm") || osArch.startsWith("aarch64")) {
                "natives-linux-${if (osArch.contains("64") || osArch.startsWith("armv8")) "arm64" else "arm32"}"
            } else {
                "natives-linux"
            }
            skijaNatives = "skija-linux"
        }
        OperatingSystem.MAC_OS -> {
            val osArch = System.getProperty("os.arch")
            lwjglNatives = "natives-macos"
            skijaNatives = if (osArch.startsWith("arm") || osArch.startsWith("aarch64")) {
                "skija-macos-arm64"
            } else {
                "skija-macos-x64"
            }
        }
        OperatingSystem.WINDOWS -> {
            val osArch = System.getProperty("os.arch")
            lwjglNatives = if (osArch.contains("64")) {
                "natives-windows${if (osArch.startsWith("aarch64")) "-arm64" else ""}"
            } else {
                "natives-windows-x86"
            }
            skijaNatives = "skija-windows"
        }
        else -> throw IllegalStateException("Unsupported operating system")
    }
    for (module in listOf("lwjgl", "lwjgl-assimp", "lwjgl-glfw", "lwjgl-openal", "lwjgl-opengl", "lwjgl-stb", "lwjgl-yoga")) {
        client("org.lwjgl:$module:3.3.0")
        clientRuntime("org.lwjgl:$module:3.3.0:$lwjglNatives")
    }
    client("io.github.humbleui:skija-shared:0.98.1")
    clientRuntime("io.github.humbleui:$skijaNatives:0.98.1")
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "16"
        freeCompilerArgs = listOf()
    }
}

fun ShadowJar.setManifest(side: String) {
    manifest {
        attributes(
            "Multi-Release" to "true",
            "Main-Class" to "com.martmists.launch.Main",
            "Game-Type" to side,
            "Game-Version" to project.version
        )
    }
}

val shadowJar by tasks.named<ShadowJar>("shadowJar") {
    classifier = ""
    configurations = listOf(
        common,
        commonRuntime,
        client,
        clientRuntime,
        server,
        serverRuntime
    )
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
    setManifest("*")
}

val serverJar = tasks.create<ShadowJar>("serverJar") {
    from(sourceSets["main"].output) {
        exclude("com/martmists/client/")
    }
    classifier = "server"
    configurations = listOf(
        common,
        commonRuntime,
        server,
        serverRuntime
    )
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
    setManifest("server")
}

val clientJar = tasks.create<ShadowJar>("clientJar") {
    from(sourceSets["main"].output) {
        exclude("com/martmists/server/")
    }
    classifier = "client"
    configurations = listOf(
        common,
        commonRuntime,
        client,
        clientRuntime
    )
    exclude("META-INF/*.SF", "META-INF/*.RSA", "META-INF/*.DSA")
    setManifest("client")
}

tasks.named("build") {
    dependsOn(shadowJar, serverJar, clientJar)
}

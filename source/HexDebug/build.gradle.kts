plugins {
    `java-library`
    `maven-publish`
    idea
    id("net.neoforged.moddev") version "2.0.141"
    id("org.jetbrains.kotlin.jvm") version "2.2.0" // Настройте версию под ваш Kotlin
}

val modId: String by project
val modVersion: String by project
val modGroupId: String by project
val neoVersion: String by project
val parchmentMappingsVersion: String by project
val parchmentMinecraftVersion: String by project
val minecraftVersion: String by project
val minecraftVersionRange: String by project
val neoVersionRange: String by project
val loaderVersionRange: String by project
val modName: String by project
val modLicense: String by project
val modAuthors: String by project
val modDescription: String by project

version = modVersion
group = modGroupId

repositories {
    mavenLocal()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
}

base {
    archivesName.set(modId)
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
kotlin.jvmToolchain(21)
neoForge {
    version = neoVersion

    parchment {
        mappingsVersion = parchmentMappingsVersion
        minecraftVersion = parchmentMinecraftVersion
    }

    runs {
        register("client") {
            client()
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }
        register("data") {
            data()
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}
sourceSets.main.get().resources.srcDir("src/generated/resources")

dependencies {
    val lsp4jVersion = "0.24.0"

    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:$lsp4jVersion")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j.jsonrpc:$lsp4jVersion")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j.debug:$lsp4jVersion")
    implementation("thedarkcolour:kotlinforforge-neoforge:5.3.0")
    implementation(fileTree(mapOf("dir" to "${rootProject.rootDir}/libs", "include" to "*.jar")))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "neo_version" to neoVersion,
        "neo_version_range" to neoVersionRange,
        "loader_version_range" to loaderVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to modVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription
    )
    inputs.properties(replaceProperties)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties + mapOf("project" to project))
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
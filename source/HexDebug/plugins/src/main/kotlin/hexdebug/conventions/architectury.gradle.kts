package hexdebug.conventions

import hexdebug.hexdebugProperties
import hexdebug.libs

plugins {
    id("hexdebug.conventions.architectury-base")
    id("hexdebug.utils.OTJFPOPKPCPBP")
}

val platform: String by project

base.archivesName = "${hexdebugProperties.modId}-$platform"

loom {
    silentMojangMappingsLicense()
}

dependencies {
    mappings(loom.layered {
        officialMojangMappings()
        // parchment(libs.parchment) // УБРАНО — используем только Mojmap
    })
}

sourceSets {
    main {
        kotlin {
            srcDir(file("src/main/java"))
        }
        resources {
            srcDir(file("src/generated/resources"))
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}
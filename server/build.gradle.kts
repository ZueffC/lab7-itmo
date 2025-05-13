import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("itmo.lab5.server.Server")
}

dependencies {
    implementation(project(":shared"))
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.slf4j:slf4j-api:2.0.9") 
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    jar {
        archiveFileName.set("server.jar")
        manifest {
            attributes(mapOf("Main-Class" to "itmo.lab5.server.Server"))
        }
    }

    register<ShadowJar>("ShadowJarServer") {
        archiveFileName.set("server-all.jar")
        destinationDirectory.set(jar.get().destinationDirectory)

        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.getByName("runtimeOnly"))
        mergeServiceFiles()
    }
}

import org.gradle.api.tasks.JavaExec
import org.gradle.api.logging.LogLevel
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

application {
    mainClass.set("itmo.lab5.client.App")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}

dependencies {
    implementation(project(":shared"))
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "itmo.lab5.client.App"
    }
}

tasks.run.configure {
    standardInput = System.`in`
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
            attributes(mapOf("Main-Class" to "itmo.lab5.client.App"))
        }
    }

    register<ShadowJar>("NewShadowJar") {
        archiveFileName.set("server-all.jar")
        destinationDirectory.set(jar.get().destinationDirectory)

        from(sourceSets.main.get().output)
        configurations = listOf(project.configurations.getByName("runtimeOnly"))
        mergeServiceFiles()
    }
}

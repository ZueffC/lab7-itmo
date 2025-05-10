import org.gradle.api.tasks.JavaExec
import org.gradle.api.logging.LogLevel

plugins {
    java
    application
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

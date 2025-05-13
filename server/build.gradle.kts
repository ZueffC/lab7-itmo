plugins {
    java
    application
}

application {
    mainClass.set("itmo.lab5.server.Server")
}

dependencies {
    implementation(project(":shared"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

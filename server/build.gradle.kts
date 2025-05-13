plugins {
    java
    application
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

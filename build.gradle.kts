plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.0-RC" // mirai-console version
}

mirai {
    coreVersion = "2.0-RC" // mirai-core version
}

group = "com.github.juzeon.qgeb"
version = "1.0"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}
dependencies {
    implementation("com.google.code.gson:gson:+")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.jsoup:jsoup:1.13.1")
}
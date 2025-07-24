plugins {
    id("java")
}

group = "me.fertiz.netflux"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
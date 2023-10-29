plugins {
    java
    application
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.sigpipe:jbsdiff:1.0")
    compileOnly(files("src/lib/Allay-API-0.0.1-all.jar"))// todo move maven
}

val isSnapshot = project.version.toString().endsWith("-SNAPSHOT")
val mainClass = "io.papermc.paperclip.Main"
project.setProperty("mainClassName", mainClass)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    val prefix = "paperclip.libs"
    listOf("org.apache", "org.tukaani", "io.sigpipe").forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }

    exclude("META-INF/LICENSE.txt")
    exclude("META-INF/NOTICE.txt")
    archiveClassifier.set("")
}

tasks.distTar {
    enabled = false
}
tasks.startScripts {
    enabled = false
}
tasks.distZip {
    enabled = false
}

tasks.jar {
    enabled = false
}

tasks.register("printVersion") {
    doFirst {
        println(version)
    }
}

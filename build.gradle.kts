plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka") version "1.4.32" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

allprojects {
    repositories {
        mavenCentral()
    }
}


subprojects {

    plugins.withId("org.jetbrains.kotlin.jvm") {
        dependencies {
            "compileOnly"(kotlin("stdlib"))
            "compileOnly"(kotlin("stdlib-jdk8"))
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }


    plugins.withType<JavaPlugin> {

        tasks.withType<Test> {
            useJUnitPlatform()
            systemProperty("java.io.tmpdir", "$buildDir/tmp")
        }

        plugins.withType<MavenPublishPlugin> {

            with(the<JavaPluginExtension>()) {
                withSourcesJar()
                withJavadocJar()
            }

            with(the<PublishingExtension>()) {
                publications.create<MavenPublication>("mavenJava") {
                    from(components["java"])
                }
            }
        }
    }


    plugins.withType<MavenPublishPlugin> {

        plugins.apply(SigningPlugin::class)

        val publishing = the<PublishingExtension>()

        publishing.publications.withType<MavenPublication> {
            pom {
                val githubRepo = providers.gradleProperty("githubRepo")
                val githubUrl = githubRepo.map { "https://github.com/$it" }

                name.set(providers.gradleProperty("projectName"))
                description.set(providers.gradleProperty("projectDescription"))
                url.set(providers.gradleProperty("projectUrl"))
                licenses {
                    license {
                        name.set(providers.gradleProperty("projectLicenseName"))
                        url.set(providers.gradleProperty("projectLicenseUrl"))
                    }
                }
                developers {
                    developer {
                        name.set(providers.gradleProperty("developerName"))
                        email.set(providers.gradleProperty("developerEmail"))
                        url.set(providers.gradleProperty("developerUrl"))
                    }
                }
                scm {
                    url.set(githubUrl.map { "$it/tree/master" })
                    connection.set(githubRepo.map { "scm:git:git://github.com/$it.git" })
                    developerConnection.set(githubRepo.map { "scm:git:ssh://github.com:$it.git" })
                }
                issueManagement {
                    url.set(githubUrl.map { "$it/issues" })
                    system.set("GitHub")
                }
            }
        }

        publishing.repositories {
            maven {
                name = "local"
                url = uri("${rootProject.buildDir}/repos/releases")
            }
        }

        with(the<SigningExtension>()) {
            sign(publishing.publications)
        }
    }


    plugins.withId("org.jetbrains.dokka") {

        dependencies {
            "dokkaJavadocPlugin"("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.20")
        }

        tasks.withType<Jar>().matching { it.name == "javadocJar" }
            .configureEach {
                from(tasks.named("dokkaJavadoc"))
            }

        tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
            dokkaSourceSets {
                named("main") {
                    sourceLink {
                        val githubUrl = project.extra["github.url"] as String
                        localDirectory.set(project.file("src/main/kotlin"))
                        remoteUrl.set(java.net.URL("$githubUrl/tree/master/"))
                        remoteLineSuffix.set("#L")
                    }
                }
            }
        }
    }
}


nexusPublishing {
    repositories {
        sonatype()
    }
}

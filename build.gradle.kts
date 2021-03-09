plugins {
    kotlin("jvm") apply false
    id("com.jfrog.bintray") version "1.8.5" apply false
    id("org.jetbrains.dokka") version "1.4.20" apply false
}

allprojects {
    repositories {
        jcenter()
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
                publications {
                    register<MavenPublication>("mavenJava") {
                        from(components["java"])
                    }
                }
            }

            plugins.withId("com.jfrog.bintray") {
                with(the<com.jfrog.bintray.gradle.BintrayExtension>()) {
                    setPublications("mavenJava")
                }
            }
        }
    }


    plugins.withId("com.jfrog.bintray") {

        with(the<com.jfrog.bintray.gradle.BintrayExtension>()) {

            user = project.extra["bintray.user"] as String
            key = project.extra["bintray.key"] as String
            dryRun = (project.extra["bintray.dryRun"] as String).toBoolean()

            pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {

                val githubUrl = project.extra["github.url"] as String

                repo = project.extra["bintray.repo"] as String
                name = project.name
                desc = project.description
                websiteUrl = githubUrl
                setLicenses("MIT")
                val labels = (project.extra["bintray.labels"] as String)
                    .split(',').map { it.trim() }
                setLabels(*labels.toTypedArray())

                vcsUrl = githubUrl
                issueTrackerUrl = "$githubUrl/issues"
                publicDownloadNumbers = true

                version(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.VersionConfig> {
                    name = project.version.toString()
                    released = java.util.Date().toString()
                    vcsTag = "v${project.version}"
                })
            })
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

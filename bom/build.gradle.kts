plugins {
    `java-platform`
    `maven-publish`
}


dependencies {
    constraints {
        api(project(":gradle-plugin-integration-test-utils"))
        api(project(":gradle-plugin-test-utils"))
        api(project(":gradle-plugin-utils"))
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenPom") {
            from(components["javaPlatform"])
            artifactId = "gradle-plugin-utils-bom"
        }
    }
}

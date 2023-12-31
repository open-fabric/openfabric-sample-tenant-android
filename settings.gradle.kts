pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/open-fabric/openfabric-unilateral-sdk")
            credentials {
                username = extra["gpr.user"] as String? ?: System.getenv("MAVEN_OF_USERNAME")
                password = extra["gpr.key"] as String? ?: System.getenv("MAVEN_OF_TOKEN")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "Unilateral Sample"
include(":app")
 
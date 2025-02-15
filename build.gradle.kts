// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0") // Firebase Plugin
    }
}

plugins {
    id("com.android.application") version "8.8.0" apply false
    id("com.android.library") version "8.8.0" apply false
    id("org.sonarqube") version "6.0.1.5171"

}

sonarqube {
    properties {

        property("sonar.token", "sqp_17210fbe55defb6a3ca7f91fc64883259c7e5352")
        property ("sonar.projectKey", "task4")
        property ("sonar.host.url", "http://localhost:9000")
        property ("sonar.projectVersion", "1.0")
        property ("sonar.sources", "src/main/java")
        property ("sonar.tests", "src/test/java")
        property ("sonar.test.inclusions", "**/*Test*.java")
        property ("sonar.java.binaries", "$buildDir/intermediates/javac/debug/classes")
        property ("sonar.junit.reportPaths", "$buildDir/test-results/testDebugUnitTest/TEST-*.xml")

    }

    }

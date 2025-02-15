// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0") // Firebase Plugin
    }
}

plugins {
    id("com.android.application") version "8.5.2" apply false
    id("com.android.library") version "8.5.2" apply false
    id("org.sonarqube") version "4.0.0.2929"

}

sonarqube {
    properties {
        property("sonar.projectKey", "task4")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "sqp_17210fbe55defb6a3ca7f91fc64883259c7e5352")

    }
}
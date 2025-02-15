plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // ✅ Make sure this is applied
}

android {
    namespace = "com.example.task4"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.task4"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)

    // ✅ Use Firebase BOM to manage versions automatically
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

    // ✅ Firebase dependencies (do NOT specify versions manually)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx")

    // ✅ Add missing dependency for InternalAppCheckTokenProvider
    implementation("com.google.firebase:firebase-appcheck-interop:16.0.0")

    // ✅ Remove outdated Firebase versions (BOM handles this)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    // ✅ JUnit for Unit Testing (for Java)
    testImplementation ("junit:junit:4.13.2")

    // ✅ Optional: Mockito for Mocking
    testImplementation ("org.mockito:mockito-core:4.11.0")

    // ✅ AndroidX Test for Instrumented Tests
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
}

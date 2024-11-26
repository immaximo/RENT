plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mobilecomputing"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobilecomputing"
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
    implementation(libs.firebase.database)
    implementation ("com.google.android.libraries.places:places:4.1.0")
    implementation(libs.firebase.storage)
    implementation ("com.squareup.picasso:picasso:2.8")
    dependencies {
        implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
        implementation("com.google.firebase:firebase-auth")
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        implementation(libs.firebase.database)
        implementation(libs.firebase.storage)
        implementation(libs.glide) // Add Glide library
        annotationProcessor(libs.glide.compiler) // Add Glide compiler
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }

}
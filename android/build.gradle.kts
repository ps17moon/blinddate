import org.gradle.api.tasks.Delete
import org.gradle.api.file.Directory

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

allprojects {
    repositories {
        google()
        mavenCentral()
        flatDir {
            dirs("libs") // ✅ 여기가 중요!!
        }
    }
}

android {
    namespace = "com.example.blinddate"
    compileSdk = 33
    ndkVersion = "27.0.12077973"
    sourceSets["main"].manifest.srcFile("app/src/main/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.example.blinddate"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}


dependencies {
    implementation(mapOf("name" to "agora-rtc-sdk-full", "ext" to "aar")) // ✅ .aar 파일 추가
    implementation("androidx.annotation:annotation:1.2.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.json:json:20230227")
}

// ✅ 추가 빌드 디렉토리 설정 유지 (괜찮아요)
val newBuildDir: Directory = rootProject.layout.buildDirectory.dir("../../build").get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}

subprojects {
    project.evaluationDependsOn(":app")
}

tasks.register<Delete>("cleaning") {
    delete(rootProject.layout.buildDirectory)
}

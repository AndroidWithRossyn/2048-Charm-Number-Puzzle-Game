plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.dokka)
}

android {
    namespace = "com.rossyn.blocktiles.game2048"
    compileSdk = 35

    defaultConfig {
        applicationId = namespace
        minSdk = 24
        targetSdk = 35
        versionCode = 1
         versionName = "1.0.0"
//        versionName = "0.dev.testing"
        vectorDrawables.useSupportLibrary = true
        renderscriptTargetApi = 24
        renderscriptSupportModeEnabled = true
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        buildConfig = true
        viewBinding = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    packaging {
        jniLibs.useLegacyPackaging = false
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    allprojects {
        gradle.projectsEvaluated {
            tasks.withType<JavaCompile> {
                options.compilerArgs.add("-Xlint:unchecked")
            }
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.multidex)

    implementation(libs.easytoast)

    implementation(libs.sdp.android)
    implementation(libs.ssp.android)

    implementation(libs.view)
    implementation(libs.animations)

    implementation(libs.toolargetool)



    //timber
    implementation(libs.timber)

    debugImplementation(libs.leakcanary.android)

    dokkaPlugin(libs.android.documentation.plugin)


    implementation (libs.androidx.lifecycle.process)

    implementation(libs.androidx.security.crypto)
}
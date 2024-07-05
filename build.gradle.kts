buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath(libs.plugin.androidgradleplugin)
        classpath(libs.plugin.kotlin)
        classpath(libs.plugin.publish)
    }
}


allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {

    tasks.withType(Test::class.java).all {
        testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}
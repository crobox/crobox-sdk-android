
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

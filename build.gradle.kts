// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

    val room_version = "2.6.1"
    id("androidx.room") version "$room_version" apply false
}
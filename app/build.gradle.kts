plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android { namespace = "com.gaurav.armagicworld"; compileSdk = 35
 defaultConfig { applicationId = "com.gaurav.armagicworld"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.1" } }
dependencies { implementation("androidx.core:core-ktx:1.15.0"); implementation("androidx.activity:activity-ktx:1.9.3") }
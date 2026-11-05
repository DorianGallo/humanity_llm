
# Task 5.8: CI/CD Pipeline & Release ProGuard Rules

## Context
Project: humanity_llm
Root: /mnt/obunaga/back_up/Sync/humanity_llm
Goal: Finalize Phase 5 by setting up a GitHub Actions CI pipeline to automatically build the APK on every push, and ensure the `proguard-rules.pro` file contains the necessary keep rules for Hilt, Room, and Kotlinx Serialization so the minified release build doesn't crash.

## Files to Create/Modify (ONLY these)
- CREATE: `.github/workflows/build.yml`
- MODIFY: `app/proguard-rules.pro`

## Requirements

### Requirement 1: Create GitHub Actions Workflow
Create the file `.github/workflows/build.yml` with the following content to build the debug APK on every push and pull request:

```yaml
name: Android CI

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: gradle
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Build Debug APK
      run: ./gradlew assembleDebug --no-daemon
      
    - name: Upload APK
      uses: actions/upload-artifact@v4
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### Requirement 2: Update ProGuard Rules for Release Build
Open `app/proguard-rules.pro`.
Append the following keep rules to the bottom of the file to ensure Hilt, Room, and Kotlinx Serialization survive R8 minification:

```proguard
# Hilt
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class *

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.jegly.offlineLLM.**$$serializer { *; }
-keepclassmembers class com.jegly.offlineLLM.** {
    *** Companion;
}
-keepclasseswithmembers class com.jegly.offlineLLM.** {
    kotlinx.serialization.KSerializer serializer(...);
}
```

## Constraints
- Do NOT modify `build.gradle.kts` or `settings.gradle.kts`.
- ONE tool call at a time.

## Verification
After OpenCode completes, the human will upload the modified files for audit, then run `./gradlew :app:assembleDebug`.
```


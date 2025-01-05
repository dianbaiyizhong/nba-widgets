import groovy.json.JsonSlurper
import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.nntk.nba.widgets"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nntk.nba.widgets"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation(project(":module-espn"))
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("org.jsoup:jsoup:1.15.3")

    implementation(project(":module-nba-2k15-circle"))
    implementation(project(":module-nba-2k16-circle"))
//    implementation(project(":module-nba-2k15"))
//    implementation(project(":module-nba-2k16"))
    implementation(project(":module-looped-logos-circle"))
    implementation("com.orhanobut:logger:2.2.0")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    implementation("com.google.guava:guava:33.3.0-android")

    testCompileOnly("org.projectlombok:lombok:1.18.32")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.53")
    implementation("com.blankj:utilcodex:1.31.1")
    implementation(libs.material)
    implementation("io.github.cymchad:BaseRecyclerViewAdapterHelper:3.0.14")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}




@Suppress("UNCHECKED_CAST")
tasks.register("createLayoutXml") {

    fun createSimpleLayout(teamName: String, size: Int, dialColor: String): String {
        val template = StringBuilder()

        template.append(
            """
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    tools:ignore="ContentDescription"
    android:layout_height="match_parent">

    <ViewFlipper
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:autoStart="true"
        android:flipInterval="40">

    """
        )
        for (i in 0 until size) {

            template.append(
                """
                        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:src="@mipmap/simple_circle_${teamName}_${i.toString().padStart(3, '0')}" />
                
            """
            )
        }

        template.append(
            """
                    </ViewFlipper>

        <ImageView
        android:id="@+id/vf_simple_logo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="?android:selectableItemBackground"
        android:src="@mipmap/glass_circle" />
        
                <AnalogClock
                android:id="@+id/analogClock"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                 android:scaleX="0.9"
                 android:scaleY="0.9"
                android:dial="@drawable/clock_bg"
                android:dialTint="$dialColor"
                        android:hand_hourTint="$dialColor"
                    android:hand_minuteTint="$dialColor"
                    android:hand_secondTint="$dialColor"
                 android:hand_minute="@drawable/hour_minute"
                 android:hand_hour="@drawable/clock_hour"
                android:hand_second="@drawable/clock_second">

            </AnalogClock>
        
        
        

</FrameLayout>
        """.trimIndent()
        )

        return template.toString()
    }


    fun create2015MovieLayout(teamName: String, size: Int): String {
        val template = StringBuilder()

        template.append(
            """
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    tools:ignore="ContentDescription"
    android:layout_height="match_parent">

    <ViewFlipper
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:autoStart="true"
        android:flipInterval="40">

    """
        )
        for (i in 0 until size) {

            template.append(
                """
                        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:src="@mipmap/movie_2015_circle_${teamName}_${i.toString().padStart(5, '0')}" />
                
            """
            )
        }

        template.append(
            """
                    </ViewFlipper>

        <ImageView
        android:id="@+id/vf_logo_player"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="?android:selectableItemBackground"
        android:src="@mipmap/glass_circle" />
        
                        <AnalogClock
                android:id="@+id/analogClock"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                 android:scaleX="0.9"
                 android:scaleY="0.9"
                android:dial="@drawable/clock_bg"
                android:dialTint="#ffffff"
                        android:hand_hourTint="#ffffff"
                    android:hand_minuteTint="#ffffff"
                    android:hand_secondTint="#ffffff"
                 android:hand_minute="@drawable/hour_minute"
                 android:hand_hour="@drawable/clock_hour"
                android:hand_second="@drawable/clock_second">

            </AnalogClock>
</FrameLayout>
        """.trimIndent()
        )

        return template.toString()
    }

    fun create2016MovieLayout(teamName: String, size: Int): String {
        val template = StringBuilder()

        template.append(
            """
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    tools:ignore="ContentDescription"
    android:layout_height="match_parent">

    <ViewFlipper
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:autoStart="true"
        android:flipInterval="20">

    """
        )
        for (i in 0 until size) {

            template.append(
                """
                        <ImageView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:src="@mipmap/movie_2016_circle_${teamName}_${i.toString().padStart(5, '0')}" />
                
            """
            )
        }

        template.append(
            """
                    </ViewFlipper>

        <ImageView
        android:id="@+id/vf_logo_player"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:background="?android:selectableItemBackground"
        android:src="@mipmap/glass_circle" />
        
                        <AnalogClock
                android:id="@+id/analogClock"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                 android:scaleX="0.9"
                 android:scaleY="0.9"
                android:dial="@drawable/clock_bg"
          android:dialTint="#ffffff"
                        android:hand_hourTint="#ffffff"
                    android:hand_minuteTint="#ffffff"
                    android:hand_secondTint="#ffffff"
                 android:hand_minute="@drawable/hour_minute"
                 android:hand_hour="@drawable/clock_hour"
                android:hand_second="@drawable/clock_second">

            </AnalogClock>
</FrameLayout>
        """.trimIndent()
        )

        return template.toString()
    }


    fun createEspnLayout(teamName: String, size: Int): String {

        val template = StringBuilder()

        template.append(
            """
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/espn_anim_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ViewFlipper
        android:id="@+id/vf"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:autoStart="true"
        android:flipInterval="20"
        tools:ignore="MissingConstraints">

    """
        )
        for (i in 0 until size) {

            template.append(
                """
            

        <ImageView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="centerCrop"
            android:src="@mipmap/nba_on_espn_${teamName}_${i.toString().padStart(5, '0')}" />
            """
            )
        }

        template.append(
            """
                    </ViewFlipper>

</LinearLayout>
        """.trimIndent()
        )
        return template.toString()
    }

    doFirst {
        val logoFile = file("src/main/res/raw/logo.json")
        val logoJson = JsonSlurper()
            .parseText(logoFile.readText()) as List<Map<String, Any>>
        for (item in logoJson) {
            val teamName: String = item["teamName"].toString()
            var outputFile =
                file("../module-looped-logos-circle/src/main/res/layout/widget_layout_${teamName}.xml")
            outputFile.printWriter().use { out ->
                out.println(
                    createSimpleLayout(
                        teamName,
                        item["simpleFrameSize"].toString().toInt(),
                        item["dialColor"].toString()
                    )
                )
            }

            outputFile =
                file("../module-nba-2k15-circle/src/main/res/layout/movie_layout_2015_${teamName}.xml")
            outputFile.printWriter().use { out ->
                out.println(
                    create2015MovieLayout(
                        teamName,
                        item["movie2015FrameSize"].toString().toInt(),

                        )
                )
            }
            outputFile =
                file("../module-nba-2k16-circle/src/main/res/layout/movie_layout_2016_${teamName}.xml")
            outputFile.printWriter().use { out ->
                out.println(
                    create2016MovieLayout(
                        teamName,
                        item["movie2016FrameSize"].toString().toInt(),

                        )
                )
            }


            println(item["espnAnimSize"])
            if (item["espnAnimSize"] == null) {
                continue
            }
            outputFile =
                file("../module-espn/src/main/res/layout/espn_anim_layout_${teamName}.xml")
            outputFile.printWriter().use { out ->
                out.println(
                    createEspnLayout(
                        teamName,
                        item["espnAnimSize"].toString().toInt(),
                    )
                )
            }


        }


    }
}




tasks.preBuild.dependsOn("createLayoutXml")



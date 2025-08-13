import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import java.net.URI
import java.util.zip.GZIPInputStream

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.mpclipboard.mpclipboard"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material3.android)
    implementation(libs.mpclipboard.rustls.platform.verifier)
    implementation(libs.androidx.glance.appwidget)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.glance.preview)
    debugImplementation(libs.androidx.glance.appwidget.preview)
}

val downloadFile: (url: String, target: File) -> Unit = { url, target ->
    if (!target.exists()) {
        println("Downloading $url ...")
        target.parentFile.mkdirs()
        URI(url).toURL().openStream().use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}

val releaseUrl = "https://github.com/mpclipboard/generic-client/releases/download/v0.1.0"
val archiveUrl = "$releaseUrl/aarch64-linux-android.tar.gz"
val headerUrl = "$releaseUrl/mpclipboard-generic-client.h"

val downloadAndExtractPrebuilt by tasks.registering {
    val outputDir = layout.buildDirectory.dir("native/prebuilt")
    val archiveFile = outputDir.get().file("aarch64-linux-android.tar.gz")
    val headerFile = outputDir.get().file("include/mpclipboard-generic-client.h")

    outputs.dir(outputDir)

    doLast {
        // Download both files
        downloadFile(archiveUrl, archiveFile.asFile)
        downloadFile(headerUrl, headerFile.asFile)

        // Extract only .a files from archive
        val tarStream = TarArchiveInputStream(GZIPInputStream(archiveFile.asFile.inputStream()))
        tarStream.use { tar ->
            var entry = tar.nextTarEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name.endsWith(".a")) {
                    val outFile = outputDir.get().file("lib/${entry.name.substringAfterLast("/")}").asFile
                    outFile.parentFile.mkdirs()
                    outFile.outputStream().use { out -> tar.copyTo(out) }
                }
                entry = tar.nextTarEntry
            }
        }
    }
}

val generateJniDefines by tasks.registering {
    val inputKtFile = file("src/main/java/org/mpclipboard/mpclipboard/JniBridge.kt")
    val outputHeader = layout.buildDirectory.file("native/prebuilt/include/jni_aliases.h")

    inputs.file(inputKtFile)
    outputs.file(outputHeader)

    doLast {
        val fileContent = inputKtFile.readText()
        val regex = Regex("""external\s+fun\s+(\w+)\s*\(""")

        fun jniMangle(part: String): String {
            return part
                .replace("_", "_1")
                .replace("$", "_00024")
        }

        val defines = regex.findAll(fileContent).map { match ->
            val methodName = match.groupValues[1]

            val packageParts = listOf("org", "mpclipboard", "mpclipboard").map(::jniMangle)
            val className = jniMangle("JniBridge\$Companion")  // explicitly encode $Companion

            val jniSymbol = buildString {
                append("Java_")
                append(packageParts.joinToString("_"))
                append("_")
                append(className)
                append("_")
                append(jniMangle(methodName))
            }

            "#define j_$methodName $jniSymbol"
        }.toList()

        val headerFile = outputHeader.get().asFile
        headerFile.parentFile.mkdirs()
        headerFile.writeText(defines.joinToString("\n"))

        println("Generated JNI defines: ${headerFile.absolutePath}")
    }
}

tasks.named("preBuild") {
    dependsOn(downloadAndExtractPrebuilt)
    dependsOn(generateJniDefines)
}

tasks.withType<com.android.build.gradle.tasks.ExternalNativeBuildTask>().configureEach {
    dependsOn(downloadAndExtractPrebuilt)
}

tasks.named("clean").configure {
    doLast {
        val cxxDir = file("${projectDir}/.cxx")
        if (cxxDir.exists()) {
            println("Deleting CMake cache: $cxxDir")
            cxxDir.deleteRecursively()
        } else {
            println("No CMake cache to delete at $cxxDir")
        }
    }
}

# Generic Android client

[![](https://jitpack.io/v/mpclipboard/mpclipboard-android.svg)](https://jitpack.io/#mpclipboard/mpclipboard-android)

This repository implements a generic library for Android. This is not the app itself.

Unfortunately there's no way to keep track of the clipboard unless you are the keyboard app (custom IME). Implementing a keyboard app just to track clipboard changes is a huge task, so instead we provide a pluggable library that can be integrated into other open-source apps.

List of patched apps:

1. [`android-florisboard`](https://github.com/mpclipboard/android-florisboard) for [`FlorisBoard`](https://florisboard.org/)

### Installation:

The library is hosted on [JitPack](https://jitpack.io/#mpclipboard/mpclipboard-android), the link contains installation instructions.

### API

This library wraps the same [`generic-client`](https://github.com/mpclipboard/generic-client) using [FFI](/mpclipboard/src/main/cpp/clipboard_jni.c) and wraps it later with [Kotlin API](/mpclipboard/src/main/java/org/mpclipboard/mpclipboard/MPClipboard.kt).

Build scripts automatically download the static library and the header from `generic-client` repo (android-arm64 build) and compiles them into dynamic library that is later loaded by Android runtime.

Additionally the library provides:

1. settings screen written in Jetpack Compose that can be rendered in the target IME app
2. connectivity widget for your Home Screen

<details>

<summary>Settings screen</summary>

![settings](/assets/settings.jpg)

</details>

<details>

<summary>Widget</summary>

![widget](/assets/widget.jpg)

</details>

### Integration into existing IME app

In short, the API is not only designed to be minimal but also to have as few interactions with existing code as possible:

1. add JitPack to the list of repositories somewhere in `settings.gradle.kts`
2. add `implementation 'com.github.mpclipboard:mpclipboard-android:Tag'` to dependencies list
3. make sure the app has permission for `INTERNET`
4. change existing IME service to create an Android's `ClipboardManager` and subscribe to it
5. instantiate `MPClipboard` class provided by this library
6. subscribe to its changes and do bi-directional copying between local clipboard and MPClipboard
7. register a widget

Patched version of https://github.com/mpclipboard/android-florisboard is a "good enough" reference implementation. Clone it, apply a patch and check the diff, there are only 105 LOC and all of them are additions, so you don't need to understand how FlorisBoard works.

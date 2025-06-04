cmake_minimum_required(VERSION 3.18.1)
project("sharedclipboard")

# Adjust to point to your downloaded native library location
set(PREBUILT_DIR ${CMAKE_SOURCE_DIR}/../../../build/native/prebuilt)
message(CMAKE_SOURCE_DIR="${CMAKE_SOURCE_DIR}")
message(PREBUILT_DIR="${PREBUILT_DIR}")

# Include directory for the .h file
include_directories(${PREBUILT_DIR}/include)

# Declare the prebuilt static library (.a file)
add_library(shared_clipboard_client STATIC IMPORTED)
set_target_properties(shared_clipboard_client PROPERTIES
        IMPORTED_LOCATION ${PREBUILT_DIR}/lib/libshared_clipboard_client_generic.a
)

# Your JNI shared lib (use your actual source file name here)
add_library(clipboardjni SHARED
        clipboard_jni.c
)

# Link JNI wrapper with the static library and Android log
target_link_libraries(clipboardjni
        shared_clipboard_client
        log
)

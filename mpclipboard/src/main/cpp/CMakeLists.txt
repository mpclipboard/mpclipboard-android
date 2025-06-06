cmake_minimum_required(VERSION 3.18.1)
project("mpclipboard")

set(PREBUILT_DIR ${CMAKE_SOURCE_DIR}/../../../build/native/prebuilt)
message(CMAKE_SOURCE_DIR="${CMAKE_SOURCE_DIR}")
message(PREBUILT_DIR="${PREBUILT_DIR}")

include_directories(${PREBUILT_DIR}/include)

add_library(mpclipboard STATIC IMPORTED)
set_target_properties(mpclipboard PROPERTIES
        IMPORTED_LOCATION ${PREBUILT_DIR}/lib/libmpclipboard_generic_client.a
)

add_library(clipboardjni SHARED clipboard_jni.c)

target_link_libraries(clipboardjni mpclipboard log)

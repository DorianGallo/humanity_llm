cmake_minimum_required(VERSION 3.22.1)
project(zim_engine)

# Include real libzim headers
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# Import pre-built libzim as an IMPORTED shared library
# ${ANDROID_ABI} resolves to arm64-v8a or x86_64 automatically
add_library(libzim SHARED IMPORTED)
set_target_properties(libzim PROPERTIES
    IMPORTED_LOCATION ${CMAKE_CURRENT_SOURCE_DIR}/libs/${ANDROID_ABI}/libzim.so
)

# Build our JNI bridge
add_library(zim_engine SHARED
    zim_bridge.cpp
)

find_library(log-lib log)

target_link_libraries(zim_engine
    libzim
    ${log-lib}
)
cmake_minimum_required(VERSION 3.15)

if(COMMAND cmake_policy)
    cmake_policy(SET CMP0003 NEW)
endif()



project(xplat-launcher)


add_subdirectory(../SDL ./build-sdl)

find_library(SDL2 SDL2)

if(ANDROID)
add_library(SDLLoader SHARED SDLLoader.c)
SET(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
else()
add_executable(launcher launcher.c)
add_executable(SDLLoader SDLLoader.c)
endif()



find_library(LIBTCC tcc ../tinycc-build)
include_directories(../tinycc)

if(WIN32)
    list(APPEND link_list mingw32)
endif()



list(APPEND link_list ${LIBTCC})
list(APPEND link_list SDL2main SDL2)

target_link_libraries(SDLLoader ${link_list})



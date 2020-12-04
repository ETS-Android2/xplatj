# XPLATJ

XPLATJ is a trial project aim to make a cross-platform layer to create application (This project is still in early stage.).

This project contains libgdx,and some native library are extracted from libgdx.

## How to build

### prerequire
C Compiler(GCC CLANG)

CMake

GNU Shell(MSYS,MSYS2 or Cygwin on Windows)

OpenJDK

Gradle

Android SDK

Android NDK

SDL2 Source

### step1
```
cd $XPLATJ_SOURCE_ROOT
export ANDROID_NDK=AndroidNdkDir  #set Android ndk location
export ANDROID_HOME=AndroidSdkDir #set Android sdk location
cp $SDL_SOURCE_ROOT $XPLATJ_SOURCE_ROOT/SDL  #copy SDL source
cd launcher
export targetsysname=javase-lwjgl #set target platform, can be one of android,javase-lwjgl
$SHELL build.sh
```

If target for javase-lwjgl, you should also modify the config file ${XPLATJ_SOURCE_ROOT}/javase-lwjgl/config.gradle depend on your target platform.

### step2
On Windows/Linux etc... You can find the distrubution in ${XPLATJ_SOURCE_ROOT}/launcher/dist

On Android, You can find the distrubution in ${XPLATJ_SOURCE_ROOT}/android-project/build/outputs/apk/release

### note
Msys1 may miss cygpath which required by gradle, you can simplely implement it by print the first arguement.

To use dependency in maven, Use tools/install-local-maven.sh to install maven dependency to local repository.

Linux is still not supported, but should be easy to do. The libgdx native libraries required by linux was also packed into jar file.

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

TinyCC Source

### step1
```
cd $XPLATJ_SOURCE_ROOT
export ANDROID_NDK=AndroidNdkDir  #set Android ndk location
export ANDROID_HOME=AndroidSdkDir #set Android sdk location
cp $SDL_SOURCE_ROOT $XPLATJ_SOURCE_ROOT/SDL  #copy SDL source
cp $TINYCC_SOURCE_ROOT $XPLATJ_SOURCE_ROOT/tinycc  #copy tinycc source

cd launcher
export targetsysname=windows #set target platform, can be one of android,windows,linux

export CPU=x86_64  # set target CPU arch, to build tinycc. can be one of i386,x86_64,arm,aarch64,riscv64
# CPU is forcely set to arm when target to Android. view build script in tinycc-build/configure.sh to build for other CPU.

export CC=GCC
export CXX=G++
export AR=ar
# set compiler environment variable. For example. on Android, you may need to set like below
# CC="$ANDROID_NDK/toolchains/llvm/prebuilt/$toolchainPlat/bin/armv7a-linux-androideabi19-clang -fPIE -fPIC"
# CXX="$ANDROID_NDK/toolchains/llvm/prebuilt/$toolchainPlat/bin/armv7a-linux-androideabi19-clang++ -fPIE -fPIC"
# AR=$ANDROID_NDK/toolchains/llvm/prebuilt/$toolchainPlat/bin/llvm-ar


$SHELL build.sh
```

If target to windows,linux, you should also modify the config file ${XPLATJ_SOURCE_ROOT}/javase-lwjgl/config.gradle depend on your target platform.

### step2
On Windows/Linux etc... You can find the distrubution in ${XPLATJ_SOURCE_ROOT}/launcher/dist

On Android, You can find the distrubution in ${XPLATJ_SOURCE_ROOT}/android-project/build/outputs/apk/release

### note
Msys1 may miss cygpath which required by gradle, you can simplely implement it by print the first input arguement.

To use dependency in maven, Use tools/install-local-maven.sh to install maven dependency to local repository.

Linux is still not fully supported, but should be easy to do. The libgdx native libraries required by linux was also packed into jar file.

### Two backend
You can switch the backend by modify the config file "$RESOURCE_DIR/flat" in generated package. The first word control the backend and can be one of gdx or sdl.

When use gdx backend, xplatj load classfile refer by cfg.ini

When use SDL backebd, xplatj compile "$RESOURCE_DIR/boot0.c" and load this file , then run the entry function _start(void *)
Some symbol will added to the context. View launcher/SDLLoader.c for more detail. 

On windows/linux target:
RESOURCE_DIR=./res 

On android target:
RESOURCE_DIR=/sdcard/xplat 

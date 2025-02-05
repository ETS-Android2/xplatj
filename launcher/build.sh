
if test -z "$buildsysname"
then
  buildsysname=$(uname -o)
fi

if test -z "$targetsysname"
then
  echo unknown targetsysname
fi

if $MAKE -v
then
  case $buildsysname in
    Msys)
    CmakeGenerator='MSYS Makefiles'
    ;;
    Linux)
    CmakeGenerator='Unix Makefiles'
    ;;
    *)
    echo Unsupport buildsysname. choose one of below
    echo msys windows linux
    exit
    ;;
  esac
elif mingw32-make -v
then
  CmakeGenerator='MinGW Makefiles'
else
  echo not support build system: $buildsysname
  exit
fi

echo "G: $CmakeGenerator"

if test -z "$CPU"
then
  export CPU=`uname -m`
fi

case $targetsysname in
  windows*|linux*)
  export targetos=$targetsysname
  $SHELL ../tinycc-build/configure.sh
  if ! cmake -S . -B build -G "$CmakeGenerator" -DCMAKE_BUILD_TYPE=RELEASE
  then
    echo cmake fail.
    exit
  fi
  ;;
  android*)
    export targetos=android
    $SHELL ../tinycc-build/configure.sh
	case $CPU in
	  arm64|aarch64)
	  androidabi=arm64-v8a
	;;
	  arm|armv7l)
	  androidabi=armeabi-v7a
	;;
	esac
    if ! cmake \
    -DCMAKE_TOOLCHAIN_FILE=$ANDROID_NDK/build/cmake/android.toolchain.cmake \
    -DANDROID_ABI=$androidabi -DANDROID_NATIVE_API_LEVEL=21 \
    -DCMAKE_BUILD_TYPE=RELEASE\
    -S . -B build -G "$CmakeGenerator"
    then
      echo cmake fail.
      exit
    fi
  ;;
  *)
    echo unsupport targetsysname $targetsysname
    exit
  ;;
esac



cd build

if test "$CmakeGenerator" = "MinGW Makefiles"
then
  mingw32-make
else
  make
fi

cd ..

case $targetsysname in
  android*)
  rm -R ../android-project/src/main/java/org/libsdl
  cp -r ../SDL/android-project/app/src/main/java/org/libsdl ../android-project/src/main/java/org/libsdl
  mkdir ../android-project/src/main/jniLibs/$androidabi
  cp build/build-sdl/*.so ../android-project/src/main/jniLibs/$androidabi
  cp build/libSDLLoader.so ../android-project/src/main/jniLibs/$androidabi
  cd ../android-project
  gradle assembleRelease
  ;;
  windows*|linux*)
  if test ! -d dist
  then
    mkdir dist
  fi

  cd ../javase-lwjgl-project
  gradle distTar
  
  cd ../launcher
  
  tar -xf ../javase-lwjgl-project/build/distributions/xplatj.tar -C ./dist
  

  cp build/launcher.exe dist/xplatj
  cp build/build-sdl/SDL2.dll dist/xplatj
  cp build/SDLLoader.exe dist/xplatj/bin
  ;;
esac



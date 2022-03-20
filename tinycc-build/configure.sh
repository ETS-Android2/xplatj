
cd `dirname $0`

if test -z $MAKE ;
then
  MAKE=make
fi

if test -z $SHELL ;
then
  SHELL=sh
fi

cd ../tinycc

if test -f PATCH_NOTE.sh 
then
  source PATCH_NOTE.sh
else
  echo '# This file tell config tool tinycc has been patched.' > PATCH_NOTE.sh 
fi

if test B$TINYCC_CONFIGURE_PATCHED != B1
then
# patch tinycc configure
  sed -i 's/targetos=`uname`//g' configure
  echo 'TINYCC_CONFIGURE_PATCHED=1' >> PATCH_NOTE.sh 
fi

cd ../tinycc-build

case $targetos in
  windows*)
  export targetos=MINGW
esac

case $targetos in
  android*)
  cp android-config.mak ../tinycc/config.mak
  cp android-config.h ../tinycc/config.h
  cd ../tinycc
  $MAKE clean
  $MAKE libtcc.a
  cp libtcc.a ../tinycc-build/libtcc.a
  cd ../tinycc-build
  ;;
  *)
  cd ../tinycc
  $SHELL configure --cc=$CC --ar=$AR --cpu=$CPU
  $MAKE clean
  $MAKE libtcc.a
  cp libtcc.a ../tinycc-build/libtcc.a
  cd ../tinycc-build
  ;;
esac



version='1.0-SNAPSHOT'

cd ../common
gradle build

mvn install:install-file -DgroupId=localrepo.pursuer -DartifactId=xplatj-common -Dversion=$version -Dpackaging=jar -Dfile=build/libs/xplatj-common.jar

mvn install:install-file -DgroupId=localrepo.pursuer -DartifactId=xplatj-gdx -Dversion=$version -Dpackaging=jar -Dfile=src/main/jar/gdx.jar
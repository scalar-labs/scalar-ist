rm -rf docker-files/scalar-ist-loader/contracts_and_functions/build
../contracts_and_functions/gradlew build

SOURCE=../contracts_and_functions/build/classes/java/main/
TARGET=docker-files/scalar-ist-loader/contracts_and_functions/build/classes/java/main

mkdir -p $TARGET && cp -r $SOURCE $TARGET
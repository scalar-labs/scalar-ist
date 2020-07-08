cd ../
./gradlew build

echo "Function登録, Initialize"
./gradlew cucumber_initialize

echo "RegisterCompany"
./gradlew cucumber_register_company

echo "テストケース実行用のユーザ登録"
./gradlew cucumber_upsert_user_profile_prepare

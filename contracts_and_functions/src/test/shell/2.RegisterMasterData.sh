cd ../../../
./gradlew build

echo "利用目的を登録"
./gradlew cucumber_register_purpose

echo "利用目的を更新"
./gradlew cucumber_update_purpose

echo "データセットスキーマを登録"
./gradlew cucumber_register_data_set_schema

echo "データセットスキーマを更新"
./gradlew cucumber_update_data_set_schema

echo "第三者提供先を登録"
./gradlew cucumber_register_third_party

echo "第三者提供先を更新"
./gradlew cucumber_update_third_party

echo "データリテンションポリシーを登録"
./gradlew cucumber_register_data_retention_policy

echo "データリテンションポリシーを更新"
./gradlew cucumber_update_data_retention_policy

echo "便益を登録"
./gradlew cucumber_register_benefit

echo "便益を更新"
./gradlew cucumber_update_benefit

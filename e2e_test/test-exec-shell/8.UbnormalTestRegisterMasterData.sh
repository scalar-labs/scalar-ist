cd ../
./gradlew build

echo "利用目的を登録 - sysope"
./gradlew cucumber_register_purpose_sysope

echo "利用目的を更新 - sysope"
./gradlew cucumber_update_purpose_sysope

echo "データセットスキーマを登録 - sysope"
./gradlew cucumber_register_data_set_schema_sysope

echo "データセットスキーマを更新 - sysope"
./gradlew cucumber_update_data_set_schema_sysope

echo "第三者提供先を登録 - sysope"
cucumber_register_third_party_sysope

echo "第三者提供先を更新 - sysope"
cucumber_update_third_party_sysope

echo "Adminが他社の第三者提供先を更新"
cucumber_update_third_party_admin_notowncompany

echo "データリテンションポリシーを登録 - sysope"
./gradlew cucumber_register_data_retention_policy_sysope

echo "データリテンションポリシーを更新 - sysope"
./gradlew cucumber_update_data_retention_policy_sysope

echo "便益を登録 - sysope"
./gradlew cucumber_register_benefit_sysope

echo "便益を更新 - sysope"
./gradlew cucumber_update_benefit_sysope

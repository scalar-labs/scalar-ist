cd ../../../
./gradlew build

echo "利用目的を登録 - Processor"
./gradlew cucumber_register_purpose_processor

echo "利用目的を更新 - Processor"
./gradlew cucumber_update_purpose_processor

echo "データセットスキーマを登録 - Processor"
./gradlew cucumber_register_data_set_schema_processor

echo "データセットスキーマを更新 - Processor"
./gradlew cucumber_update_data_set_schema_processor

echo "データリテンションポリシーを登録 - Processor"
./gradlew cucumber_register_data_retention_policy_processor

echo "データリテンションポリシーを更新 - Processor"
./gradlew cucumber_update_data_retention_policy_processor

echo "便益を登録 - Processor"
./gradlew cucumber_register_benefit_processor

echo "便益を更新 - Processor"
./gradlew cucumber_update_benefit_processor

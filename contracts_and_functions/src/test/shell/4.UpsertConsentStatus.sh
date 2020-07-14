cd ../../../
./gradlew build

echo "同意の登録"
./gradlew cucumber_upsert_consent_status_register

echo "同意の更新"
./gradlew cucumber_upsert_consent_status_update

echo "同意の取得 - データ主体"
./gradlew cucumber_get_consent_status_data_subject

echo "同意の取得 - Controller"
./gradlew cucumber_get_consent_status_controller

echo "同意の取得 - Processor"
./gradlew cucumber_get_consent_status_processor

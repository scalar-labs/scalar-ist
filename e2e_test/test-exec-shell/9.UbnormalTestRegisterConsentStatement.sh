cd ../
./gradlew build

echo "同意文書を登録 - sysope"
./gradlew cucumber_register_consent_statement_sysope

echo "同意文書を修正 - sysope"
./gradlew cucumber_update_consent_statement_revision_sysope

echo "修正する同意文書が存在しない"
./gradlew cucumber_update_consent_statement_revision_notfound

echo "同意文書を改訂 - sysope"
./gradlew cucumber_update_consent_statement_version_sysope

echo "改訂する同意文書が存在しない"
./gradlew cucumber_update_consent_statement_version_notfound

echo "同意文書のステータスを変更 - sysope"
./gradlew cucumber_update_consent_statement_status_sysope

echo "ステータスを変更する同意文書が存在しない"
./gradlew cucumber_update_consent_statement_status_notfound

echo "変更する同意文書のステータスが不正"
./gradlew cucumber_update_consent_statement_status_invalidstatus

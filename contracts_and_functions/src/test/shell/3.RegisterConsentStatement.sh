cd ../../../
./gradlew build

echo "同意文書を登録"
./gradlew cucumber_register_consent_statement

echo "同意文書を修正"
./gradlew cucumber_update_consent_statement_revision

echo "同意文書を改訂"
./gradlew cucumber_update_consent_statement_version

echo "同意文書のステータスを変更"
./gradlew cucumber_update_consent_statement_status

echo "同意文書を取得"
./gradlew cucumber_get_consent_statement

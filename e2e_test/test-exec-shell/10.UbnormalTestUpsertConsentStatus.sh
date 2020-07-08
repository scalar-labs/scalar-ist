cd ../
./gradlew build

echo "同意の登録 - 対象の同意文書が存在しない"
./gradlew cucumber_upsert_consent_status_register_notfound

echo "同意の更新 - 対象の同意文書が存在しない"
./gradlew cucumber_upsert_consent_status_update_notfound

echo "同意の取得 - 対象の同意文書が存在しない"
./gradlew cucumber_get_consent_status_notfound

echo "同意の取得 - 引数にcompany_idが指定されていない、かつ、取得する同意記録が自身の同意記録ではない"
./gradlew cucumber_get_consent_status_notown_notcompanyid

echo "同意の取得 - 引数にcompany_idが指定されている、かつ、取得する同意記録が自身の所属する事業者の同意記録ではない"
./gradlew cucumber_get_consent_status_notowncompany

echo "同意の取得 - 参照可能な権限を持つロールではない"
./gradlew cucumber_get_consent_status_admin

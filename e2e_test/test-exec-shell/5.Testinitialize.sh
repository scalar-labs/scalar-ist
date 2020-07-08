cd ../
./gradlew build

echo "RegisterCompany by SysOpe"
./gradlew cucumber_register_company_sysope

echo "Insert SysAd SysOpe Admin User Profile by SysAd"
./gradlew cucumber_upsert_user_profile_sysad_sysope_admin

echo "Insert SysOpe Admin User Profile by SysOpe"
./gradlew cucumber_upsert_user_profile_sysope_admin

echo "Insert Admin Controller Processor User Profile by Admin"
./gradlew cucumber_upsert_user_profile_admin_controller_processor

echo "Update SysAd SysOpe Admin User Profile by SysAd"
./gradlew cucumber_upsert_user_profile_sysad_sysope_admin_update

echo "Update SysOpe Admin User Profile by SysOpe"
./gradlew cucumber_upsert_user_profile_sysope_admin_update

echo "Update Admin Controller Processor User Profile by Admin"
./gradlew cucumber_upsert_user_profile_admin_controller_processor_update

echo "UpdateCompany"
./gradlew cucumber_update_company

echo "SysOpeユーザが自社の事業者情報を更新"
./gradlew cucumber_update_company_sysope_owncompany

echo "SysOpeユーザが他社の事業者情報を更新"
./gradlew cucumber_update_company_sysope_notowncompany

echo "組織情報を更新"
./gradlew cucumber_upsert_organization

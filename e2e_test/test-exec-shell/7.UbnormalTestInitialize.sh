cd ../
./gradlew build

echo "Initialize - Sysope"
./gradlew cucumber_initialize_sysope

echo "RegisterCompany - Admin"
./gradlew cucumber_register_company_admin

echo "Adminが自身が所属しない事業者に対してUpdateCompany"
./gradlew cucumber_update_company_admin

echo "組織情報を更新 - SysOpe"
./gradlew cucumber_upsert_organization_sysope

echo "Adminが自身が所属しない事業者に対して組織情報を更新"
./gradlew cucumber_upsert_organization_admin_notowncompany

echo "SysAdが付与できないロールを付与する"
./gradlew cucumber_upsert_user_profile_from_sysad_to_controller

echo "SysOpeが付与できないロールを付与する"
./gradlew cucumber_upsert_user_profile_from_sysope_to_controller

echo "Adminが所属する事業者でない事業者のユーザ情報を登録する"
./gradlew cucumber_upsert_user_profile_admin_controller_processor_from_admin_notowncompany

echo "Adminが所属する事業者でない事業者のユーザ情報を更新する"
./gradlew cucumber_upsert_user_profile_admin_controller_processor_from_admin_notowncompany_update

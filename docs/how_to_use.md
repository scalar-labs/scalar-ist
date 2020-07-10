# Scalar IST のユーザーストーリー

ISTでは、事業者の種類としてシステム運用事業者と個人情報取扱事業者の2つがあり、システム運用事業者は1事業者のみ登録し、個人情報取扱事業者の登録を行う権限を持つ。
個人情報取扱事業者は、個人情報の収集、利用、提供を行う事業者であり、個人情報の収集のため同意文書の作成、および同意記録の管理を行う。

ISTでは、以下の順番で事業者、およびユーザープロファイルを登録する

1. システムを運用するシステム運用事業者を登録し、システム運用事業者に所属するシステム管理者とシステム運用者のユーザープロファイルを登録
1. システムを利用する個人情報取扱事業者の登録、個人情報取扱事業者の管理者、情報管理者、情報処理者ユーザープロファイルを登録
1. 個人情報取扱事業者のユーザーがマスター項目（利用目的、データセットスキーマ、データリテンションポリシー、便益、第三者提供先）、同意文書を登録

## ユーザーストーリー
## システム運用事業者を登録
1. システム運用事業者の事業者情報、組織情報を登録
1. システム管理者、システム運用者のユーザープロファイル情報を登録

## 個人情報取扱事業者を登録
1. 事業者情報、事業者の管理組織情報を登録

## 個人情報取扱事業者のユーザのユーザープロファイル情報を登録
1. 管理者のユーザープロファイル情報を登録
1. 情報処理者のユーザープロファイル情報を登録

## 同意文書のマスタ情報を登録
1. 利用目的を登録、更新
1. 利用期限を登録、更新
1. データを利用する第三者提供先を登録、更新
1. データセットスキーマを登録、更新
1. 便益を登録、更新

## 同意文書の登録、更新
1. 同意文書を登録
1. 同意文書を修正（再同意が不要な変更内容）
1. 同意文書を改訂（再同意が必要な変更内容）

## 事業者情報の更新
1. ユーザの所属組織・ロールの更新
1. 組織情報の追加・更新

## データ主体による同意の記録
1. データ主体が同意文書に対して同意、拒否の記録を行う
1. データ主体が自身の同意状態を取得する
1. 事業者のユーザーが自事業者の同意文書に対する同意状態を取得する

# デプロイツールを使ったユーザーストーリーの実行

## デプロイツールのビルド
```
$ ./gradlew installDist
```

## ISTで使用する共有のファンクションを登録
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/functions.json
```

# システム運用事業者、個人情報取扱事業者の情報を登録
## システム運用事業者を登録
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/initialize.json
```

## 個人情報取扱事業者の登録
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_company.json
```

## 個人情報取扱事業者のユーザプロファイル情報を登録
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_user_profile_controller.json
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_user_profile_admin.json
```

# 同意文書のマスタ情報を登録
## 利用目的を登録する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_purpose.json
```

## 利用目的を更新する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_purpose.json
```

## データセットスキーマを登録する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_data_set_schema.json
```

## データセットスキーマを更新する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_data_set_schema.json
```

## 第三者提供先を登録する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_third_party.json
```

## 第三者提供先を更新する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_third_party.json
```

## 利用停止、データ削除期限を登録する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_data_retention_policy.json
```

## 利用停止、データ削除期限を更新する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_data_retention_policy.json
```

## 便益を登録する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_benefit.json
```

## 便益を更新する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_benefit.json
```

# 同意文書の登録、更新
## 同意文書を登録する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/register_consent_statement.json
```

## 同意文書を修正する（再同意が不要な変更内容）
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_consent_statement_revision.json
```

## 同意文書を改訂する（再同意が必要な変更内容）
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_consent_statement_version.json
```

## 同意文書のステータスを変更する
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_consent_statement_status.json
```

# 事業者情報の更新
## 事業者ユーザの所属組織の更新
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/update_company.json
```

## 事業者ユーザのロールの更新
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_user_profile_controller_add_processor.json
```

## 組織情報の追加・更新
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_organization.json
```

# データ主体による同意の記録
## 同意の登録
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_consent_status_register.json
```

## 同意の更新
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/upsert_consent_status_update.json
```

## データ主体により同意の状態の参照
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/get_consent_status_data_subject.json
```

## 事業者ユーザーによる同意の状態の参照
```
$ build/install/deploy_tool/bin/deploy_tool -f build/resources/main/command/get_consent_status_controller.json
```





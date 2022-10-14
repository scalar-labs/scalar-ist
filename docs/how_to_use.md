# Scalar ISTの実行手順
Scalar ISTの実行のためには、以下の作業を行う必要があります。

- コントラクト実行者のHolder ID、秘密鍵、証明書を作成
- コントラクト実行者の証明書をScalar DLTに登録する
- コントラクト実行者が使用するコントラクト、ファンクションをScalar DLTに登録する
- コントラクト実行者の秘密鍵を使用してコントラクト実行時のリクエストデータの電子署名を生成する
- Scalar DLTにリクエストデータと電子署名を送信し、コントラクトを実行する

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

### Set up Scalar DL Java Client SDK

Download the `scalardl-java-client-sdk` zip file from the [release](https://github.com/scalar-labs/scalardl-java-client-sdk/releases) to `scalar-ist`.
Then unzip and rename it to `scalardl-java-client-sdk`.
```console
wget -O ./scalardl-java-client-sdk.zip https://github.com/scalar-labs/scalardl-java-client-sdk/releases/download/v<SCALARDL_JAVA_CLIENT_VERSION>/scalardl-java-client-sdk-<SCALARDL_JAVA_CLIENT_VERSION>.zip
unzip scalardl-java-client-sdk.zip
mv scalardl-java-client-sdk-* scalardl-java-client-sdk
```
* Don't forget to update `<SCALARDL_JAVA CLIENT_VERSION>` with the specified Scalar DL version.

## ISTで使用する共有のファンクションを登録

You will first need to build the contract and functions
```console
cd contracts_and_functions
./gradlew build
cd ../tools/deploy
```

Then register the functions
```console
./functions
```

# システム運用事業者、個人情報取扱事業者の情報を登録
## システム運用事業者を登録
```console
./initialize
```

## 個人情報取扱事業者の登録
```console
./register_company
```

## 個人情報取扱事業者のユーザプロファイル情報を登録
```console
./upsert_user_profile_admin
./upsert_user_profile_controller 
```

# 同意文書のマスタ情報を登録
## 利用目的を登録する
```console
./register_purpose
```

## 利用目的を更新する
```console
./update_purpose
```

## データセットスキーマを登録する
```console
./register_data_set_schema
```

## データセットスキーマを更新する
```console
./update_data_set_schema
```

## 第三者提供先を登録する
```console
./register_third_party
```

## 第三者提供先を更新する
```console
./update_third_party
```

## 利用停止、データ削除期限を登録する
```console
./register_data_retention_policy
```

## 利用停止、データ削除期限を更新する
```console
./update_data_retention_policy
```

## 便益を登録する
```console
./register_benefit
```

## 便益を更新する
```console
./update_benefit
```

# 同意文書の登録、更新
## 同意文書を登録する
```console
./register_consent_statement
```

## 同意文書を修正する（再同意が不要な変更内容）
```console
./update_consent_statement_revision
```

## 同意文書を改訂する（再同意が必要な変更内容）
```console
./update_consent_statement_version
```

## 同意文書のステータスを変更する
```console
./update_consent_statement_status
```

## 同意文書に対する修正（再同意が不要な変更）の履歴を取得します。
```console
./get_consent_statement_history
```

# 事業者情報の更新
## 事業者ユーザの所属組織の更新
```console
./update_company
```

## 事業者ユーザのロールの更新
```console
./upsert_user_profile_controller_add_processor
```

## 組織情報の追加・更新
```console
./upsert_organization
```

# データ主体による同意の記録
## 同意の登録
```console
./upsert_consent_status_register
```

## 同意の更新
```console
./upsert_consent_status_update
```

## データ主体により同意の状態の参照
```console
./get_consent_status_data_subject
```

## 事業者ユーザーによる同意の状態の参照
```console
./get_consent_status_controller
```

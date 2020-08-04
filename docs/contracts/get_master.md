# マスターの取得(Contract)
同意文書に紐付ける Mater (Purpose, DatasetSchema, DataRetentionPolicy, Benefit) の内容を取得します。
取得可能なマスターのアセットは、Contract Propertiesで指定します。

個人情報取扱事業者が実行した場合はアセットの内容を全て返却し、データ主体が実行した場合は個人情報取扱事業者に係わる情報は削除して返却します。

| 項目名 | 内容 |
| ----- | ------ |
|Contract Name|UpdateConsentStatementRevision|
|Contract Argument| JSON Schema (see below) |
|Function Name|n/a|
|Function Argumen|n/a|
|Return| content of Asset |

# 権限
情報処理者（Processor）、情報管理者（Controller）のロールを持つ実行者、データ主体が実行できます。
データ主体の実行は、ハッシュ化されたIDによる実行のみ許可します。

# アセット
取得対象のアセットは、同意文書のマスターとして指定する以下のアセットです。
- Porpose
- Data Set Schema
- Data Retention Policy
- Benefit

# テーブル
テーブルの参照は行いません。

## Asset ID Format
**properties.asset_name + properties.asset_version + "-" + "組織ID" + "-" + "文書番号"**

| 項目名 | 値 | 書式 | サンプル | 補足説明 |
| ----- | -- | ---- | -------- | -------- |
| アセット名 | properties.asset_name |  固定文字列 | "pp" | 
| バージョン | properties.asset_version | NN | "01" | |
| 組織ID | 組織ID | UUID | "a5e9971d-32be-490d-bff4-c6d65816c1e5" |  |
| 文書番号 | 登録時のUNIX Epch Time（ミリ秒） | BIGINT型の数値 | 1573098580650 | milliseconds since the UNIX epoch |

## Contract Properties

| 項目名      | データ型 | 必須 | 書式         | 説明                        | サンプル |
| ---------- | ------- | --- | ----------  | --------------------------- | ------ |
| permitted_asset_names | TEXT    | ○   | Array(TEXT) | コントラクトに操作を許可するアセット名の配列 | ["pp", "ds", "rp", "bn"] |
| remove_columns | TEXT  | ○   | Array(TEXT) | ハッシュIDで指定した場合、返却値から削除するカラム名の配列 | ["company_id", "organization_id", "created_by", "purpose_id", "data_set_schema_id", "data_retention_policy_id", "benefit_id"] |

## Contract Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
| asset_id | TEXT    | ○   | TEXT |        | コントラクトが操作するアセット名 | "bn" |
| is_hashed | BOOLEAN | ○   | BOOLEAN |        | アセットIDがハッシュ化されているか | true |
| company_id | TEXT  | ×   | TEXT | user_profile.company_id | 個人情報取扱事業者ID | scalar-labs.com |

## Function Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
|n/a|||||||

# Contract Process

| 処理 | 説明 | エラー処理 |
| --- | ---- | -------- |
|コントラクト固有の情報を変数にセットする<br> ロール：`Controller`、`Processor` | ROLES = ["Processor", "Controller"] |  |
| **[contract_argument.is_hashed = falseの場合]** |  |  |
| ContractArgumentを確認 | `contract_argument.company_id` が指定されていることを確認 | company_idが指定されていない |
| 実行権限を確認 | `Controller`、`Processor` ロールが、実行者に付与されているかを確認 | ロールが付与されていない |
| アセットIDが許可されたIDであるか確認 | Asset IDの先頭の文字が `contract_properties.permitted_asset_names` で指定している文字列のいずれかと一致する事を確認 | アセットの参照権限がない |
| アセットを取得 | Asset IDをキーに `GetUserProfile` コントラクトを実行してアセットを取得し、取得したアセットを返却する | アセットが存在しない |
| アセットを返却 | 取得したアセットの `company_id` と `contract_argument.company_id` を比較し、一致していればアセットの内容を返却する | アセットの参照権限がない |
| **[contract_argument.is_hashed = trueの場合]** |  |  |
| アセットIDが許可されたIDであるか確認 | Asset IDをデコードし、デコードしたAsset IDの先頭の文字が `contract_properties.permitted_asset_names` で指定している文字列のいずれかと一致する事を確認 | アセットの参照権限がない |
| アセットを取得して返却 | デコードしたAsset IDをキーに `GetUserProfile` コントラクトを実行してアセットを取得し、アセットから`contract_properties.remove_columns` に該当する項目を削除し、コントラクト実行結果として返却する | アセットが存在しない |


# Function Process

| 処理 | 説明 | エラー処理 |
| --- | ---- | -------- |
|n/a|||


# Contract Argument Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": [
    "asset_id",
    "is_hashed"
  ],
  "properties": {
    "asset_id": {
      "type": "string",
      "pattern": "^[a-zA-Z0-9-/_.]+$"
    },
    "is_hashed": {
      "type": "boolean"
    },
    "company_id":{
        "type":"string",
        "format":"hostname"
    }
  }
}
```

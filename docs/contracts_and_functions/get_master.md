# マスターの取得(Contract)
同意文書に紐付ける Mater (Purpose, DatasetSchema, DataRetentionPolicy, Benefit, ThirdParty) の内容を取得します。

取得するAssetは、以下の条件に合致するAssetのみとします。
- Asset IDの先頭が、Contract Propertiesに指定した文字と一致する
- コントラクト実行者が所属する個人情報取扱事業者と、Assetを登録した個人情報取扱事業者が一致する

| 項目名 | 内容 |
| ----- | ------ |
|Contract Name| GetMaster |
|Contract Argument| [JSON Schema](https://github.com/scalar-labs/scalar-ist-internal/blob/master/docs/contracts_and_functions/get_master.md#contract-argument-schema) |
|Function Name|n/a|
|Function Argumen|n/a|
|Return| Content of Asset |

# Role
管理者（Admin）, 情報処理者（Processor）、情報管理者（Controller）のロールを持つ実行者

# Asset
取得対象のアセットは、同意文書のマスターとして指定する以下のアセットです。
- Purpose
- DataSetSchema
- DataRetentionPolicy
- Benefit
- ThirdParty

# Table
テーブルの参照は行いません。

## Asset ID Format
```
properties.asset_name + properties.asset_version + "-" + organization_id + "-" + document_no
```

| 項目名 | 値 | 書式 | サンプル | 補足説明 |
| ----- | -- | ---- | -------- | -------- |
| Asset Name | properties.asset_name | 固定文字列 | "pp" | 利用目的の場合は "pp" |
| Version | properties.asset_version | NN | "01" | アセットバージョン |
| Organization ID | organization_id | UUID | "a5e9971d-32be-490d-bff4-c6d65816c1e5" | 個人情報取扱事業者の組織ID |
| Document No | document_no | BIGINT | 1573098580650 | 登録時のUNIX Epch Time（ミリ秒） |

## Contract Properties

| 項目名      | データ型 | 必須 | 書式         | 説明                        | サンプル |
| ---------- | ------- | --- | ----------  | --------------------------- | ------ |
| permitted_asset_names | TEXT    | ○   | Array(TEXT) | コントラクトに操作を許可するアセット名の配列 | ["pp", "ds", "rp", "bn", "tp"] |

## Contract Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
| asset_id | TEXT    | ○   | TEXT |        | コントラクトが操作するアセット名 | "bn" |
| company_id | TEXT  | ○   | TEXT | user_profile.company_id | 個人情報取扱事業者ID | scalar-labs.com |

## Function Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
|n/a|||||||

# Contract Process

| 処理 | 説明 | エラー処理 |
| --- | ---- | -------- |
|コントラクト固有の情報を変数にセットする<br> ロール：`Admin`, `Controller`, `Processor` | ROLES = ["Admin", "Controller", "Processor"] |  |
| Contract Argumentを確認 | `contract_argument.asset_id`, `contract_argument.company_id`が指定されていることを確認 | Contract Argumentが不正 |
| 実行権限を確認 | `Admin`、`Controller`、`Processor` ロールが、実行者に付与されているかを確認 | ユーザープロファイルが取得できない<br>ロールが付与されていない |
| アセットIDが許可されたIDであるか確認 | Asset IDの先頭の文字が `contract_properties.permitted_asset_names` で指定している文字列のいずれかと一致する事を確認 | アセットの参照権限がない |
| アセットを取得 | Asset IDをキーに `GetUserProfile` コントラクトを実行してアセットを取得 | アセットが存在しない |
| アセットを返却 | 取得したアセットの `company_id` と `contract_argument.company_id` を比較し、一致していればアセットの内容を返却する | アセットの参照権限がない |

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
    "company_id"
  ],
  "properties": {
    "asset_id": {
      "type": "string",
      "pattern": "^[a-zA-Z0-9-/_.]+$"
    },
    "company_id":{
        "type":"string",
        "format":"hostname"
    }
  }
}
```

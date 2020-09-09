# 同意文書の履歴を取得(Contract)
同意文書に対する修正（再同意が不要な変更）の履歴を取得します。

取得出来る同意文書は、実行者が所属する個人情報取扱事業者が登録した同意文書のみとします。

| 項目名 | 内容 |
| ----- | ------ |
|Contract Name| GetConsentStatementHistory |
|Contract Argument| [JSON Schema](./get_consent_statement_history.md#contract-argument-schema) | |
|Function Name|n/a|
|Function Argumen|n/a|
|Return| Content of Asset |

# Role
管理者（Admin）、 情報処理者（Processor）、情報管理者（Controller）のロールを持つ実行者

# Asset
取得対象のアセットは、同意文書アセットです。
- Consent Statement

# Table
テーブルの参照は行いません。

## Asset ID Format
```
properties.asset_name + properties.asset_version + "-" + organization_id + "-" + document_no
```

| 項目名 | 値 | 書式 | サンプル | 補足説明 |
| ----- | -- | ---- | -------- | -------- |
| Asset Name | properties.asset_name | 固定文字列 | "cs" | 同意文書アセットのAsset Name |
| Version | properties.asset_version | NN | "01" | アセットバージョン |
| Organization ID | organization_id | UUID | "a5e9971d-32be-490d-bff4-c6d65816c1e5" | 個人情報取扱事業者の組織ID |
| Document No | document_no | BIGINT | 1573098580650 | 登録時のUNIX Epch Time（ミリ秒） |

## Contract Properties

| 項目名      | データ型 | 必須 | 書式         | 説明                        | サンプル |
| ---------- | ------- | --- | ----------  | --------------------------- | ------ |
| permitted_asset_names | TEXT    | ○   | Array(TEXT) | コントラクトに操作を許可するアセット名の配列 | ["cs"] |
| contract_argument_schema | TEXT    | ○   | JSON Schema | Contract Argument のフォーマットを定義したJSONスキーマ | [JSON Schema](./get_consent_statement_history.md#contract-argument-schema) |

## Contract Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
| asset_id | TEXT    | ○   | TEXT |        | 履歴を取得する同意文書ID | "cs01-a5e9971d-32be-490d-bff4-c6d65816c1e5-1573098580650" |
| company_id | TEXT  | ○   | TEXT | user_profile.company_id | 個人情報取扱事業者ID | scalar-labs.com |
| is_hashed | BOOLEAN | ○   | BOOLEAN |        | アセットIDがハッシュ化されているか | false |

## Function Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
|n/a|||||||

# Contract Process

| 処理 | 説明 | エラー処理 |
| --- | ---- | -------- |
|コントラクト固有の情報を変数にセットする<br> ロール：`Admin`, `Controller`, `Processor` | `ROLES = ["Admin", "Controller", "Processor"]` |  |
| Contract Argumentを確認 | `contract_argument.asset_id`, `contract_argument.company_id` が指定されていることを確認 | Contract Argumentが不正 |
| 実行権限を確認 | `Admin`、`Controller`、`Processor` ロールが、実行者に付与されているかを確認 | ユーザープロファイルが取得できない<br>ロールが付与されていない |
| アセットIDが許可されたIDであるか確認 | Asset IDの先頭の文字が `contract_properties.permitted_asset_names` で指定している文字列のいずれかと一致する事を確認 | アセットの参照権限がない |
| アセットを取得 | Asset IDをキーに `GetAssetRecord` コントラクトを実行してアセットを取得<br/>`mode = "scan"` を指定して全てのageを取得する | アセットが存在しない |
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
    "company_id",
    "is_hashed"
  ],
  "properties": {
    "asset_id": {
      "type": "string",
      "pattern": "^[a-zA-Z0-9-/_.]+$"
    },
    "company_id":{
        "type":"string",
        "format":"hostname"
    },
    "is_hashed": {
      "type": "boolean"
    }
  }
}
```

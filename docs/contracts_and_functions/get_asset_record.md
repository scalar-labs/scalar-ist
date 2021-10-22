# レコードを参照する(Contract)
PutAssetRecordで追加したアセットを取得します。

| 項目名 | 内容 |
| ----- | ------ |
|Contract Name| GetAssetRecord  |
|Contract Argument| [JSON Schema](./get_asset_record.md#contract-argument-schema) | |
|Function Name|n/a|
|Function Argumen|n/a|
|Return| Content of Asset |

# Role
Contract からのみ実行可能

# Asset
[contract_argument.asset_id] で指定

# Table
テーブルの参照は行いません。

## Asset ID Format
```
Hash( [ contract_argument.asset_id ] )
```

## Contract Properties

| 項目名      | データ型 | 必須 | 書式         | 説明                        | サンプル |
| ---------- | ------- | --- | ----------  | --------------------------- | ------ |
| contract_argument_schema | TEXT    | ○   | JSON Schema | Contract Argument のフォーマットを定義したJSONスキーマ | [JSON Schema](./get_asset_record.md#contract-argument-schema) |
| salt | TEXT    | ○   | TEXT | hashidで使用するsalt | "salt" |

## Contract Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
| asset_id | TEXT    | ○   | TEXT |        | 取得するアセットのAsset ID | "cs01-a5e9971d-32be-490d-bff4-c6d65816c1e5-1573098580650" |
| is_hashed | BOOLEAN  | ○   | true / false |  | Contract ArgumentのアセットIDがハッシュ化されているか<br/>true : ハッシュ化済み false : ハッシュ化していない | false |
| mode | TEXT  | × | ["get", "scan"] |  | アセット取得モード<br/>get : 最新の version のみ取得<br>scan : 全ての version を取得<br/>default : get | "get" |
| start_version | INT  | × |  |  | 取得するアセットの最小 version<br/>※ `mode = "scan"` の時のみ有効  | 2 |
| end_version | INT  | × |  |  | 取得するアセットの最大 version<br/>※ `mode = "scan"` の時のみ有効  | 5 |
| with_limit | INT  | × |  |  | 取得するアセットの version 数の条件<br/>※ `mode = "scan"` の時のみ有効  | 5 |
| version_order | TEXT  | × | Enum["ASC", "DESC"] |  | 取得するアセットの version の Ordering<br/>※ `mode = "scan"` の時のみ有効  | 5 |

## Function Argument

| 項目名      | データ型 | 必須 | 書式 | 外部参照 | 説明                       | サンプル |
| ---------- | ------- | --- | ---  | ------ | --------------------------- | ------ |
|n/a|||||||

# Contract Process

| 処理 | 説明 | エラー処理 |
| --- | ---- | -------- |
| 本コントラクトの呼び出し元をチェック | 当コントラクトが、他のコントラクトからinvokeされている事を確認し、直接実行されている場合は、エラーとする | 直接実行が許可されていないコントラクト |
| プロパティから、hashidで使用する salt 情報を読み取る | 以下のプロパティ情報を取得し、変数に格納する<br/>properties.salt | 取得対象の項目がプロパティに含まれていない |
| Contract Argumentから値を取得 | Contract Argument からアセットIDとハッシュ化済みフラグの値を取得する<br/>・アセットID ： contract_argument.asset_id<br/>・ハッシュ化済みフラグ ： contract_argument.is_hashed | ContractArgumentにasset_idが含まれていない |
| アセットIDがハッシュ化されていない場合、ハッシュ値に変換 | contract_argument.is_hashed = false の場合<br>hashid で、アセットIDをハッシュ値に変換<br/>hashed_asset_id = hashid( contract_argument.asset_id )<br/><br/>contract_argument.hashed = true の場合<br/>hashed_asset_id = contract_argument.asset_id |  |
| アセットを取得し、アセットの内容を返却する | `contract_argument.mode = "get"`、または未指定の場合<br/>最新の version のみ取得する<br/>`contract_argument.mode = "scan"` の場合<br/>アセットの複数バージョンを取得する<br/>取得する version は、`contract_argument` で指定した条件に従う | アセットが存在しない |

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
      "pattern": "^[a-zA-Z0-9-/_]+$"
    },
    "is_hashed": {
      "type": "boolean"
    },
    "mode": {
      "type": "string",
      "enum": ["get", "scan"]
    },
    "start_version": {
      "type": "int"
    },
    "end_version": {
      "type": "int"
    },
    "with_limit": {
      "type": "int"
    },
    "version_order": {
      "type": "string",
      "enum": ["asc", "desc"]
    }
  }
}
```

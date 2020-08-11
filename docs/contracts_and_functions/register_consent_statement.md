# Register Consent Statement

# User Story

## 同意文書を登録する

## What
同意文書を新規に登録し、同意文書IDを取得する
- 同意文書にバージョン名を登録する
- 同意文書にデータセット・スキーマIDを紐付ける
- 同意文書に利用目的IDを紐付ける
- 同意文書に利用期限IDを紐付ける
- 同意文書に第三者提供先IDを紐付ける
- コントラクト・プロパティに定義された「文書」のフォーマットに従って、同意文書を登録する

## Why
- 新しく同意文書を作成し、同意を行う対象となる同意文書を指定するため
- 同意文書に同意されたデータ主体から受け取ったデータを、同意内容に従って、正しく運用するため
- 同意文書が改ざんされないように保全し、同意文書を参照したいユーザーに開示するため

## Who
情報管理者（Controller）のロールを持つ実行者のみが行える


# Interface
| title | content |
| ----- | ------ |
|Contract ID|RegisterConsentStatement |
|実行者|"Controller"ロールを持つ実行者	|
|操作対象アセット|consent_statement|
|参照アセット|user_profile|
|Sub Contracts| GetUserProfile<br> PutAssetRecord"|
|Functions|RegisterConsentStatement|



## Asset ID Format
properties.asset_name + properties.asset_version + "-" + "組織ID" + "-" + "文書番号" 

| 項目名 | 値 | 書式 | サンプル | 補足説明 |
| ----- | -- | ---- | -------- | -------- |
| アセット名 | properties.asset_name |  固定文字列 | "cs" | 
| バージョン | properties.asset_version | NN | "01" | |
| 組織ID | 組織ID | UUID | "a5e9971d-32be-490d-bff4-c6d65816c1e5" |  |
| 文書番号 | 登録時のUNIX Epch Time（ミリ秒） | BIGINT型の数値 | 1573098580650 | milliseconds since the UNIX epoch |






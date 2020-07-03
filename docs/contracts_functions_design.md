# Contract/Function 設計書

## Scalar IST v2.00

<p style="text-align: right">
深津 航</p>

<p style="text-align: right">
Version 2.00 - 2020/05/08</p>

**Contents**

[TOC]
# Table of contents

- [Contract/Function 設計書](#contractfunction-)
  - [Scalar IST v2.00](#scalar-ist-v200)
- [Asset / Table](#asset--table)
  - [同意文書アセット](#)
  - [](#)
  - [同意アセット](#)
  - [利用目的アセット](#)
  - [データセットスキーマアセット](#)
  - [第三者提供先アセット](#)
  - [便益アセット](#)
  - [利用期限アセット](#)
  - [個人情報取扱事業者アセット](#)
  - [ユーザープロファイルアセット](#)
- [Contract / Function](#contract--function)
  - [同意文書（Consent Statement）](#consent-statement)
  - [同意（Consent）](#consent)
  - [利用目的（Purpose）](#purpose)
  - [データセットスキーマ（Dataset Schema）](#dataset-schema)
  - [第三者提供先（Third Party）](#third-party)
  - [便益（Benefit）](#benefit)
  - [利用期限（Data Retention Policy）](#data-retention-policy)
  - [事業者（Company）](#company)
  - [ユーザープロファイル（User Profile）](#user-profile)
  - [Appendix](#appendix)




# Asset / Table


## 同意文書アセット


### 同意文書の構造

同意文書は、以下の要件を満たす構造を持つ。



*   同意文書のメタデータ
    *   同意文書のID
    *   同意文書を管理する個人情報取扱事業者、組織
    *   共同利用先事業者ID（委託先含む）
    *   同意文書のバージョン名（主に公開日付など）
    *   同意文書の状態（Draft, Public）
    *   作成者
    *   作成日時
*   同意文書の本文
    *   タイトル
    *   概要
    *   本文
*   同意文書の変遷
    *   改定もとの同意文書ID
    *   前回からの変更内容
*   マスター情報
    *   利用目的
    *   データ項目
    *   必須第三者提供先
    *   任意に選択可能な第三者提供先
    *   利用期限
    *   任意に選択可能な利用目的
        *   利用目的
        *   データ項目
        *   必須第三者提供先
        *   任意に選択可能な第三者提供先
        *   利用期限
    *   データ主体に提供する便益

同意文書アセットは、下図のような構造を持つ。



<p id="gdcalert1" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image1.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert2">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image1.png "image_tooltip")



### 同意文書間の関係性

同意文書は、以下のような関係性を持つ



*   修正を行った場合、既存の同意文書のIDを変えずに更新する（履歴の追加）
*   改訂を行った場合、既存の同意文書とは別の同意文書を登録し、元の文書をたどれる状態にする

同意文書同士は、グループ化することができる



*   複数の同意文書を1つのグループとして管理することができる
*   同意文書のグループは、同意文書が修正・改訂されても同じグループに属する


### 同意文書に対する権限

同意文書に対するアクセスは次のような形になる。



*   登録、修正、改訂が行えるのは、個人情報管理者のみ
*   登録、修正、改訂が行える同意文書は、自社の同意文書のみ
*   ドラフトの同意文書は、自社のメンバーのみ参照可能である
*   公開された同意文書は、誰でも参照することができる


### 同意文書アセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・組織ID
<p>
・登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>parent_consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>改訂元の同意文書ID（asset_id）
   </td>
  </tr>
  <tr>
   <td>group_company_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>version
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>バージョン番号、または公開日
   </td>
  </tr>
  <tr>
   <td>status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>公開状態（”draft”もしくは”published”のどちらか）。デフォルトは、”draft”
<p>


<p id="gdcalert2" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: undefined internal link (link text: "同意文書のステータス"). Did you generate a TOC? </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert3">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>

<a href="#heading=h.u1fh6biqkow1">同意文書のステータス</a>
   </td>
  </tr>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書のタイトル
   </td>
  </tr>
  <tr>
   <td>abstract
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書の概要
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
  <tr>
   <td>consent_statement

   </td>
   <td>TEXT

   </td>
   <td>Y

   </td>
   <td>同意文書の本文（Markdown or HTML）

   </td>
  </tr>
  <tr>
   <td>optional_purposes

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>任意同意対象の利用目的

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的のタイトル
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>created_at

   </td>
   <td>BIGINT

   </td>
   <td>Y

   </td>
   <td>作成日時（ミリ秒）

   </td>
  </tr>
</table>



### 同意文書テーブルの構造

テーブル名：consent_statment


<table>
  <tr>
   <td colspan="3" >項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="3" >root_consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>基底となる同意文書の同意文書ID
   </td>
  </tr>
  <tr>
   <td colspan="3" >company_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>個人情報取事業者のドメイン名
   </td>
  </tr>
  <tr>
   <td colspan="3" >organization_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
   </td>
  </tr>
  <tr>
   <td colspan="3" >consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>同意文書ID　※同意文書のアセットID
   </td>
  </tr>
  <tr>
   <td colspan="3" >version
   </td>
   <td>TEXT
   </td>
   <td>C↑
   </td>
   <td>バージョン番号、または公開日
   </td>
  </tr>
  <tr>
   <td colspan="3" >status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>文書の公開状態。デフォルトは、"Draft"
   </td>
  </tr>
  <tr>
   <td colspan="3" >group_company_ids
   </td>
   <td>TEXT
   </td>
   <td>IDX
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td colspan="3" >title
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>同意文書のタイトル
   </td>
  </tr>
  <tr>
   <td colspan="3" >abstract
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>同意文書の概要
   </td>
  </tr>
  <tr>
   <td colspan="3" >changes
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>文書の更新理由
   </td>
  </tr>
  <tr>
   <td colspan="3" >purpose_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的のIDリスト
   </td>
  </tr>
  <tr>
   <td colspan="3" >data_set_schema_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセットスキーマのIDリスト
   </td>
  </tr>
  <tr>
   <td colspan="3" >benefit_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="3" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先IDリスト
   </td>
  </tr>
  <tr>
   <td colspan="3" >optional_third_parties
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>
   </td>
  </tr>
  <tr>
   <td colspan="6" >

<table>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="3" >data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ルールのID

   </td>
  </tr>
  <tr>
   <td colspan="3" >consent_statement

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>同意文書（利用規約、プライバシーポリシーなど）。Markdown、もしくはHTML

   </td>
  </tr>
  <tr>
   <td colspan="3" >optional_purposes

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>任意の利用目的のJSON配列

   </td>
  </tr>
  <tr>
   <td colspan="6" >

<table>
  <tr>
   <td colspan="2" >title
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意の利用目的のタイトル
   </td>
  </tr>
  <tr>
   <td colspan="2" >description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意の利用目的の説明
   </td>
  </tr>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_parties
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>
   </td>
  </tr>
  <tr>
   <td colspan="5" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyの説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="2" >data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>Data Retention Policyアセットのasset_id

   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="3" >parent_consent_statement_id

   </td>
   <td>TEXT

   </td>
   <td>IDX

   </td>
   <td>更新元となる同意文書の同意文書ID

   </td>
  </tr>
  <tr>
   <td colspan="3" >created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者のHolderID

   </td>
  </tr>
  <tr>
   <td colspan="3" >created_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>このレコードの登録日時

   </td>
  </tr>
</table>



## 


## 同意アセット

生活者（データ主体）は、同意文書に対して、次のような同意ができる。



*   文書全体に対する同意・拒否
*   文書の中の必須な利用目的に対する任意同意可能な第三者提供先の選択
*   任意同意可能な利用目的の選択
    *   任意同意する利用目的に対する任意同意可能な第三者提供先の選択

同意文書が修正・改訂された場合、同意の状態は次のようになる



*   修正の場合は、通知のみ
*   改訂の場合は、通知と再同意が必要な状態への変更

同意文書の改訂によって、再同意が要求された場合、デフォルトの同意状態を取得できる。



*   前回、文書全体に同意していた場合は、デフォルトは全体に同意
*   前回、同意項目を選択していた場合、同意していた項目がデフォルトで同意
*   新たに追加・変更されたマスター情報をリストして提示できる


### 同意の構造

同意のアセットは、生活者（データ主体）がどのように同意・拒否したのかを記録するためのアセットである。

同意アセットは、下図のような構造を持つ。



<p id="gdcalert3" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image2.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert4">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image2.png "image_tooltip")



### 同意に対する権限

同意に対するアクセスは次のような形になる。



*   登録、更新が行えるのは、同意情報のオーナーであるデータ主体のみ
*   同意情報は、誰でも参照することができる


### 


### 同意アセットの構造


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・同意文書のアセットID
<p>
・データ主体の HolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>難読化された同意文書ID
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>同意文書への同意状態
<p>
["approved", "rejected", "configured"]
<p>
approved : 同意文書に含まれる全ての利用目的に同意している
<p>
rejected : 同意文書に含まれる全ての利用目的を拒否している
<p>
configured : 同意文書に含まれる利用目的の一部に同意している
   </td>
  </tr>
  <tr>
   <td colspan="4" >consented_detail
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>同意した任意利用目的の内容
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td>group_company_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>難読化された利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>dataset_schema_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>難読化されたデータセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>optional_third_party_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>data_retention_policy
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>データ保持期限に関する情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>nondeletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>使用終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
  <tr>
   <td>deletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>optional_purposes

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>任意項目への同意状態JSON

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_retention_policy
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>データ保持期限に関する情報
   </td>
  </tr>
  <tr>
   <td colspan="5" >

<table>
  <tr>
   <td>nondeletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>使用終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
  <tr>
   <td>deletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="4" >rejected_detail

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>拒否した任意利用目的の内容

   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td>optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td>optional_purposes
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意項目への同意状態JSON
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird PartyのJSON
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="4" >created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者であるデータ主体の HolderID

   </td>
  </tr>
  <tr>
   <td colspan="4" >created_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>登録日時ミリ秒

   </td>
  </tr>
</table>



### 同意テーブル

テーブル名：consent


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >data_subject_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>データ主体ID
   </td>
  </tr>
  <tr>
   <td colspan="4" >updated_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>更新日時ミリ秒
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>難読化された同意文書ID
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>同意文書への同意状態
<p>
["approved", "rejected", "configured"]
<p>
approved : 同意文書に含まれる全ての利用目的に同意している
<p>
rejected : 同意文書に含まれる全ての利用目的を拒否している
<p>
configured : 同意文書に含まれる利用目的の一部に同意している
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_id
   </td>
   <td>TEXT
   </td>
   <td>IDX
   </td>
   <td>同意ID
<p>
"consent" + "-" + 難読化された同意文書ID + "-" + データ主体ID
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_detail
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>同意した任意利用目的の内容
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td>group_company_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>難読化された利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>dataset_schema_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>難読化されたデータセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>optional_third_party_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>data_retention_policy
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>データ保持期限に関する情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>nondeletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>使用終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
  <tr>
   <td>deletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>optional_purposes

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>任意項目への同意状態JSON

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_retention_policy
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データ保持期限に関する情報
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="4" >rejected_detail

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>拒否した任意利用目的の内容

   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td>optional_purposes
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意項目への同意状態JSON
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird PartyのJSON
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="4" >created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者であるデータ主体の HolderID

   </td>
  </tr>
  <tr>
   <td colspan="4" >created_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>登録日時ミリ秒

   </td>
  </tr>
</table>



## 利用目的アセット

同意文書には、提供されるデータセットの利用目的を登録する事ができる。


### 利用目的の構造

利用目的は、以下の要件を満たす構造を持つ。



*   利用目的のメタデータ
    *   利用目的ID
    *   利用目的を管理する事業者、組織
    *   カテゴリ定義
    *   名称
    *   説明文
    *   法的文書
    *   表示文書
    *   利用ガイド
    *   その他の補足事項

利用目的アセットは、下図のような構造を持つ。



<p id="gdcalert4" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image3.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert5">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image3.png "image_tooltip")



### 利用目的アセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・組織ID
<p>
・登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>purpose_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>properties.asset_name + properties.asset_version + "-" + 組織ID + "-" + created_at
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>category_of_purpose
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の分類
<p>
TCF v2.0 などの規格名は、ここに格納する
   </td>
  </tr>
  <tr>
   <td>purpose_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的名
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>legal_text
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>法的文書
   </td>
  </tr>
  <tr>
   <td>user_friendly_text
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>表示文書
   </td>
  </tr>
  <tr>
   <td>guidance
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用ガイド
   </td>
  </tr>
  <tr>
   <td>note
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>そのほかの補足事項
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
</table>



### 利用目的同意文書テーブルの構造

テーブル名：purpose


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
<p>
※Indexでの検索時は、実行者のcompany_idでフィルタリングする
   </td>
  </tr>
  <tr>
   <td>category_of_purpose
   </td>
   <td>TEXT
   </td>
   <td>IDX
   </td>
   <td>利用目的の分類
   </td>
  </tr>
  <tr>
   <td>purpose_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的名
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>legal_text
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>法的文書
   </td>
  </tr>
  <tr>
   <td>user_friendly_text
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>表示文書
   </td>
  </tr>
  <tr>
   <td>guidance
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用ガイド
   </td>
  </tr>
  <tr>
   <td>note
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>そのほかの補足事項
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>更新日時ミリ秒
   </td>
  </tr>
</table>



### 同意文書に対する利用目的の関係性

利用目的は、同意文書に対して以下の関係性を持つ。



*   利用目的は同意文書の任意項目である
*   同意文書に利用目的を複数登録する事ができる


### 利用目的に対する権限

利用目的に対するアクセスは次のようになる



*   登録が行えるのは、情報管理者、情報処理者のみ
*   利用目的の更新を行う事が出来るユーザーは、利用目的を保有する事業者、組織に所属しているユーザーのみ


## データセットスキーマアセット


### データセットスキーマの構造

データセットスキーマは、以下の要件を満たす構造を持つ。



*   データセットスキーマのメタデータ
    *   データセットスキーマID
    *   データセットスキーマを管理する個人情報取扱事業者、組織
    *   名称
    *   説明文
    *   取扱区分
    *   種類
    *   分類
    *   データセットに含まれる項目の定義
    *   変更履歴
        *   格納先情報の変更履歴
        *   活性/不活性の変更履歴
    *   登録者のID
    *   登録日時（ミリ秒）

データセットスキーマは下図のような構造を持つ



<p id="gdcalert5" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image4.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert6">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image4.png "image_tooltip")



### データセットスキーマアセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・組織ID
<p>
・登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>data_set_schema_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>properties.asset_name + properties.asset_version + "-" + 組織ID + "-" + 登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>data_set_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセット名
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセットの説明
   </td>
  </tr>
  <tr>
   <td>category_of_data
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データの取扱区分
   </td>
  </tr>
  <tr>
   <td>data_type
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データの種類
   </td>
  </tr>
  <tr>
   <td>classification
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データの分類
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセットに含まれる項目を定義したJSON Schema
   </td>
  </tr>
  <tr>
   <td>changes
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>以下の変更履歴
<p>
・data_location に対する変更
<p>
・Active/Inactive に対する変更
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
</table>



### データセットスキーマテーブルの構造

テーブル名：data_set_schema


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>事業者ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
<p>
※Indexでの検索時は、実行者のcompany_idでフィルタリングする
   </td>
  </tr>
  <tr>
   <td>data_set_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセットの名前
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセットの説明
   </td>
  </tr>
  <tr>
   <td>data_location
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>データの格納先情報
<p>
・アクセスパス
<p>
・接続手段
<p>
・認証手段
<p>
など
   </td>
  </tr>
  <tr>
   <td>category_of_data
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データの取扱区分
   </td>
  </tr>
  <tr>
   <td>data_type
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データの種類
   </td>
  </tr>
  <tr>
   <td>classification
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データの分類
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>データセットに含まれる項目を定義したJSON Schema
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>更新日時ミリ秒
   </td>
  </tr>
</table>



### 同意文書に対するデータセットスキーマの関係性

データセットスキーマは、同意文書に対して以下の関係性を持つ。



*   データセットスキーマは同意文書の任意項目である
*   同意文書にデータセットスキーマを複数登録する事ができる


### データセットスキーマに対する権限

データセットスキーマに対するアクセスは次のようになる



*   登録が行えるのは、情報管理者、情報処理者のみ
*   データセットスキーマの更新を行う事が出来るユーザーは、データセットスキーマを保有する事業者、組織に所属しているユーザーのみ


## 第三者提供先アセット


### 第三者提供先の構造

第三者提供先は、以下の要件を満たす構造を持つ。



*   第三者提供先のメタデータ
    *   第三者提供先ID
    *   個人情報取扱事業者のドメイン名、組織
    *   第三者提供先のドメイン名
    *   第三者提供先の正式名称
    *   第三者提供先の国税庁の法人番号（オプショナル）
    *   その他の第三者提供先の情報
    *   第三者提供先の内の組織
        *   組織ID
        *   組織名
        *   組織の説明
        *   活性/非活性
    *   登録日時（ミリ秒）

第三者提供先は下図のような構造を持つ



<p id="gdcalert6" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image5.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert7">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image5.png "image_tooltip")



### 第三者提供先アセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・個人情報取扱事業者ID（個人情報取扱事業者のドメイン）
<p>
・第三者提供先のドメイン名
   </td>
  </tr>
  <tr>
   <td>third_party_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>properties.asset_name + properties.asset_version + "-" + 個人情報取扱事業者のドメイン + "-" + 第三者提供先のドメイン名
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者のドメイン名
   </td>
  </tr>
  <tr>
   <td>third_party_domain
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先のドメイン名
   </td>
  </tr>
  <tr>
   <td>third_party_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の国税庁の法人番号
   </td>
  </tr>
  <tr>
   <td>third_party_metadata
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>第三者提供先の情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の正式名称
   </td>
  </tr>
  <tr>
   <td>address
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の住所
   </td>
  </tr>
  <tr>
   <td>email
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先のメールアドレス
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>organizations

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>第三者提供先内の組織。JSON Object構造を繰り返す。

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>organization_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織名
   </td>
  </tr>
  <tr>
   <td>organization_description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織の説明
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者のHolderID

   </td>
  </tr>
  <tr>
   <td>created_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>登録日時ミリ秒

   </td>
  </tr>
</table>



### 第三者提供先テーブルの構造

テーブル名 : third_party


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>third_party_domain
   </td>
   <td>TEXT
   </td>
   <td>C↑
   </td>
   <td>提供先のドメイン名
   </td>
  </tr>
  <tr>
   <td>third_party_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の国税庁の法人番号
   </td>
  </tr>
  <tr>
   <td>third_party_metadata
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>第三者提供先の情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の正式名称
   </td>
  </tr>
  <tr>
   <td>address
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の住所
   </td>
  </tr>
  <tr>
   <td>email
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先のメールアドレス
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>organizations

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>第三者提供先内の組織。JSON Object構造を繰り返す。

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>organization_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織名
   </td>
  </tr>
  <tr>
   <td>organization_description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織の説明
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>is_active

   </td>
   <td>BOOLEAN

   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない

default : true

   </td>
  </tr>
  <tr>
   <td>created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者のHolderID

   </td>
  </tr>
  <tr>
   <td>updated_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>更新日時ミリ秒

   </td>
  </tr>
</table>



### 同意文書に対する第三者提供先の関係性

第三者提供先は、同意文書に対して以下の関係性を持つ。



*   第三者提供先は同意文書の任意項目である
*   同意文書に第三者提供先を複数登録する事ができる


### 第三者提供先に対する権限

第三者提供先に対するアクセスは次のようになる



*   登録・更新が行えるのは、管理者のみ


## 便益アセット


### 便益の構造

便益は、以下の要件を満たす構造を持つ。



*   便益のメタデータ
    *   便益ID
    *   便益を管理する事業者、組織ID
    *   便益の分類
    *   便益名
    *   便益の説明
    *   便益の提供者
    *   便益の提供時期
    *   登録者のID
    *   登録日時（ミリ秒）

便益は下図のような構造を持つ



<p id="gdcalert7" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image6.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert8">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image6.png "image_tooltip")



### 便益アセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・組織ID
<p>
・登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>benefit_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>properties.asset_name + properties.asset_version + "-" + 組織ID + "-" + created_at
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>category_of_benefit
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益の分類
   </td>
  </tr>
  <tr>
   <td>benefit_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益名
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益の説明
   </td>
  </tr>
  <tr>
   <td>provider
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益の提供者
   </td>
  </tr>
  <tr>
   <td>tern_of_provide
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益の提供時期
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
</table>



### 便益テーブルの構造

テーブル名 : benefit


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
<p>
※Indexでの検索時は、実行者のcompany_idでフィルタリングする
   </td>
  </tr>
  <tr>
   <td>category_of_benefit
   </td>
   <td>TEXT
   </td>
   <td>IDX
   </td>
   <td>便益の分類
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用期限の説明
   </td>
  </tr>
  <tr>
   <td>provider
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益の提供者
   </td>
  </tr>
  <tr>
   <td>tern_of_provide
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>便益の提供時期
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>IDX
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>更新日時ミリ秒
   </td>
  </tr>
</table>



### 同意文書に対する便益の関係性

便益は、同意文書に対して以下の関係性を持つ。



*   便益は同意文書の任意項目である
*   同意文書に便益を複数登録する事ができる


### 便益に対する権限

便益に対するアクセスは次のようになる



*   登録が行えるのは、情報管理者、情報処理者のみ
*   便益の更新を行う事が出来るユーザーは、便益を保有する事業者、組織に所属しているユーザーのみ


## 利用期限アセット


### 利用期限の構造

利用期限は、以下の要件を満たす構造を持つ。



*   利用期限のメタデータ
    *   利用期限ID
    *   利用期限を管理する個人情報取扱事業者、組織ID
    *   ポリシー名
    *   ポリシーの種類（有期/不定）
    *   利用期間
    *   保持期間
    *   利用期限の説明
    *   登録者のID
    *   登録日時（ミリ秒）

利用期限は下図のような構造を持つ



<p id="gdcalert8" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image7.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert9">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image7.png "image_tooltip")



### 利用期限アセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・個人情報取扱事業者ID
<p>
・登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>properties.asset_name + properties.asset_version + "-" + 事業者ID + "-" + 登録時の"created_at"
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>policy_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>ポリシー名
   </td>
  </tr>
  <tr>
   <td>policy_type
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>ポリシーの種類
<p>
finite : 有期 / indefinite : 不定
   </td>
  </tr>
  <tr>
   <td>length_of_use
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用期間
<p>
数字のみの指定時は日数（days）と判定し、その他のフォーマットはアプリケーションで判定する
   </td>
  </tr>
  <tr>
   <td>length_of_retention
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持期間
<p>
数字のみの指定時は日数（days）と判定し、その他のフォーマットはアプリケーションで判定する
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用期限の説明
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
</table>



### 利用期限テーブルの構造

テーブル名 : data_retention_policy


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>policy_type
   </td>
   <td>TEXT
   </td>
   <td>C↑
   </td>
   <td>ポリシーの種類
<p>
finite : 有期 / indefinite : 無期限
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
<p>
※Indexでの検索時は、実行者のcompany_idでフィルタリングする
   </td>
  </tr>
  <tr>
   <td>policy_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>ポリシー名
   </td>
  </tr>
  <tr>
   <td>length_of_use
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用期間
<p>
数字のみの指定時は日数（days）と判定し、その他のフォーマットはアプリケーションで判定する
   </td>
  </tr>
  <tr>
   <td>length_of_retention
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持期間
<p>
数字のみの指定時は日数（days）と判定し、その他のフォーマットはアプリケーションで判定する
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用期限の説明
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>IDX
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>更新日時ミリ秒
   </td>
  </tr>
</table>



### 同意文書に対する利用期限の関係性

利用期限は、同意文書に対して以下の関係性を持つ。



*   利用期限は同意文書の任意項目である
*   同意文書1つにつき利用期限を1つだけ登録する事ができる


### 利用期限に対する権限

利用期限に対するアクセスは次のようになる



*   登録が行えるのは、情報管理者、情報処理者のみ
*   利用期限の更新を行う事が出来るユーザーは、利用期限を保有する事業者、組織に所属しているユーザーのみ


## 個人情報取扱事業者アセット

ISTでは、同意文書、マスター情報を保有する事業者を登録する。


### 個人情報取扱事業者の構造

個人情報取扱事業者は、以下の要件を満たす構造を持つ。



*   個人情報取扱事業者のメタデータ
    *   事業者ID
*   個人情報取扱事業者の内容
    *   ドメイン名
    *   正式名称
    *   国税庁の法人番号
    *   事業者の情報（構造は任意のJSON）

個人情報取扱事業者アセットは、下図のような構造を持つ。



<p id="gdcalert9" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image8.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert10">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image8.png "image_tooltip")



### 個人情報取扱事業者のアセット構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者のドメイン名
   </td>
  </tr>
  <tr>
   <td>company_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>国税庁の法人番号
   </td>
  </tr>
  <tr>
   <td>company_metadata
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者の情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>address
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者の住所
   </td>
  </tr>
  <tr>
   <td>email
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者のメールアドレス
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>organizations

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>個人情報取扱事業者内の組織。JSON Object構造を繰り返す。

最初の登録時にAdmin組織が作られる

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>organization_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織名
   </td>
  </tr>
  <tr>
   <td>organization_description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織の説明
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者のHolderID

   </td>
  </tr>
  <tr>
   <td>created_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>登録日時ミリ秒

   </td>
  </tr>
</table>



### 個人情報取扱事業者のテーブル構造

テーブル名 : company


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
<p>
※個人情報取扱事業者のドメイン名を使用する
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>company_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>国税庁の法人番号（オプショナル）
   </td>
  </tr>
  <tr>
   <td>company_metadata
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者の情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>address
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者の住所
   </td>
  </tr>
  <tr>
   <td>email
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者のメールアドレス
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>is_active

   </td>
   <td>BOOLEAN

   </td>
   <td>
   </td>
   <td>有効な個人情報取扱事業者であるかのフラグ

false の場合は検索対象にならない

default : true

   </td>
  </tr>
  <tr>
   <td>created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者のHolderID

   </td>
  </tr>
  <tr>
   <td>updated_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>更新日時ミリ秒

   </td>
  </tr>
</table>



### 組織のテーブル構造

テーブル名 : organization


<table>
  <tr>
   <td colspan="2" >項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="2" >company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
<p>
※個人情報取扱事業者のドメイン名を使用する
   </td>
  </tr>
  <tr>
   <td colspan="2" >organization_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
   </td>
  </tr>
  <tr>
   <td colspan="2" >created_at
   </td>
   <td>BIGINT
   </td>
   <td>C↓
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td colspan="2" >organization_metadata
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者内の組織情報。JSON形式で保持。
   </td>
  </tr>
  <tr>
   <td colspan="5" >

<table>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>organization_name
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織名
   </td>
  </tr>
  <tr>
   <td>organization_description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織の説明
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>BOOLEAN
   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない
<p>
default : true
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="2" >is_active

   </td>
   <td>BOOLEAN

   </td>
   <td>
   </td>
   <td>false の場合は検索対象にならない

default : true

   </td>
  </tr>
  <tr>
   <td colspan="2" >created_by

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>登録者のHolderID

   </td>
  </tr>
  <tr>
   <td colspan="2" >updated_at

   </td>
   <td>BIGINT

   </td>
   <td>
   </td>
   <td>更新日時ミリ秒

   </td>
  </tr>
</table>



### 個人情報取扱事業者に対するその他の項目との関係性

個人情報取扱事業者とその他の項目は以下の関係性を持つ。



*   個人情報取扱事業者は、複数の組織を持つ
*   個人情報取扱事業者は、複数のユーザープロファイルを持つ
*   個人情報取扱事業者は、複数の同意文書を持つ
*   個人情報取扱事業者は複数のマスター項目を持つ
*   上記の項目は、事業者をまたいでアクセスすることができない


### 個人情報取扱事業者に対する権限

個人情報取扱事業者に対するアクセスは次のような形になる



*   登録が行えるのは、システム管理者（SysAdmin）とシステム運用者（SysOperator）のみ
*   更新が行えるのは、システム運用管理者（SysOperator）、個人情報取扱事業者の管理者（Admin）のみ


## ユーザープロファイルアセット

個人情報取扱事業者のユーザーが所属する組織、付与されたロールを管理する、ユーザープロファイルを登録する。


### ユーザープロファイルの構造

ユーザープロファイルは以下の要件を満たす構造を持つ。



*   ユーザープロファイルのメタデータ
    *   ユーザープロファイルを管理する個人情報取扱
    *   所属する全ての組織
    *   Holder ID
    *   付与されている全てのロール

ユーザープロファイルは、下図のような構造を持つ。



<p id="gdcalert10" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: inline image link here (to images/image9.png). Store image on your image server and adjust path/filename/extension if necessary. </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert11">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>


![alt_text](images/image9.png "image_tooltip")



### ユーザープロファイルアセットの構造


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>asset_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>以下のIDを組み合わせ、難読化されたID
<p>
・properties.asset_name
<p>
・properties.asset_version
<p>
・個人情報取扱事業者ID（個人情報取扱事業者のドメイン）
<p>
・Holder ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Holder ID
   </td>
  </tr>
  <tr>
   <td>roles
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>ユーザに与えられたロール
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
</table>



### ユーザープロファイルテーブルの構造

テーブル名 : user_profile


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>KEY
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>PK
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>C↑
<p>
IDX
   </td>
   <td>ユーザのHolderID
   </td>
  </tr>
  <tr>
   <td>organization_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>組織ID
<p>
※全体で一意になるIDを付与しておく
   </td>
  </tr>
  <tr>
   <td>roles
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>ユーザに与えられたロール
   </td>
  </tr>
  <tr>
   <td>created_by
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>登録者のHolderID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>登録日時ミリ秒
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>
   </td>
   <td>更新日時ミリ秒
   </td>
  </tr>
</table>



### ユーザープロファイルに対する利用目的の関係性

ユーザープロファイルは、以下の特性を持つ。



*   個人情報取扱事業者に所属するユーザーは、1つ以上のユーザープロファイルを持つ
*   他の個人情報取扱事業者のユーザープロファイルにはアクセスできない


### ユーザープロファイルに対する権限

ユーザープロファイルに対するアクセスは次のような形になる



*   登録、更新が行えるのは、システム管理者、システム運用者、管理者のみ
*   参照が行えるユーザープロファイルは、自身のユーザープロファイルのみ


# Contract / Function


## 同意文書（Consent Statement）

同意文書は、利用目的アセット、データセット・スキーマ・アセット、利用期限アセット、第三者提供先アセット、便益アセット、同意文書アセットで構成され、以下に示す Contract / Function を用いて操作を行う。



*   同意文書のマスターに対する操作
    *   利用目的
        *   利用目的の登録（Contract/Function）
        *   利用目的の修正（Contract/Function）
        *   利用目的の参照（Contract/Function）
        *   利用目的の利用停止・再開（Contract/Function）
    *   データセット・スキーマ
        *   データセット・スキーマの登録（Contract/Function）
        *   データセット・スキーマの修正（Contract/Function）
        *   データセット・スキーマの参照（Contract/Function）
        *   データセット・スキーマの利用停止・再開（Contract/Function）
    *   利用期限
        *   利用期限の登録（Contract/Function）
        *   利用期限の修正（Contract/Function）
        *   利用期限の参照（Contract/Function）
        *   利用期限の利用停止・再開（Contract/Function）
    *   第三者提供先
        *   第三者提供先の登録（Contract/Function）
        *   第三者提供先の修正（Contract/Function）
        *   第三者提供先の参照（Contract/Function）
        *   第三者提供先の利用停止・再開（Contract/Function）
    *   便益
        *   便益の登録（Contract/Function）
        *   便益の修正（Contract/Function）
        *   便益の参照（Contract/Function）
        *   便益の利用停止・再開（Contract/Function）
*   同意文書
    *   同意文書の登録（Contract/Function）
    *   同意文書の修正（Contract/Function）
    *   同意文書の改訂（Contract/Function）
    *   同意文書の公開（Contract/Function）
    *   同意文書の参照（Contract/Function）
*   同意文書の管理
    *   同意文書のグループ化（Function）
    *   同意文書の系統取得（親子関係、ルート）（Function）
    *   同意文書のアーカイブ（Contract）


### 同意文書の登録（Contract/Function）

同意文書の登録を行う。登録が成功すると、登録された同意文書IDを返す。同意文書の登録に当たっては、事前に参照されるマスター情報が登録されている必要があるが、別のチェックロジックを実行してマスターの登録状態を確認する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>RegisterConsentStatement
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>RegisterConsentStatement
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）ロールを持つユーザーが実行できる。登録できる同意文書は、ユーザーが所属する “company_id”（個人情報取扱事業者ID）に対してのみ。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>同意文書を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>検索用の同意文書のメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
  <tr>
   <td>asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td>asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>group_company_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>version
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>バージョン番号、または公開日
   </td>
  </tr>
  <tr>
   <td>status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>公開状態（”Draft”もしくは”Published”のどちらか）。デフォルトは、”Draft”
<p>


<p id="gdcalert11" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: undefined internal link (link text: "同意文書のステータス"). Did you generate a TOC? </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert12">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>

<a href="#heading=h.u1fh6biqkow1">同意文書のステータス</a>
   </td>
  </tr>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書のタイトル
   </td>
  </tr>
  <tr>
   <td>abstract
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書の概要
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
  <tr>
   <td>consent_statement

   </td>
   <td>TEXT

   </td>
   <td>Y

   </td>
   <td>同意文書の本文（Markdown or HTML）

   </td>
  </tr>
  <tr>
   <td>optional_purposes

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>任意同意対象の利用目的

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的のタイトル
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>created_at

   </td>
   <td>BIGINT

   </td>
   <td>Y

   </td>
   <td>作成日時（ミリ秒）

   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された同意文書IDである “hashed_asset_id” が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の同意文書ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”UUID”+"-"
<p>
 +”文書番号”
<p>
)
   </td>
  </tr>
</table>



### 同意文書の修正（Contract/Function）

既存の同意文書に対して修正（再同意が不要な変更）を行う。修正が成功すると、修正された同意文書IDを返す。同意文書の修正に当たっては、事前に参照されるマスター情報が登録されている必要があるが、別のチェックロジックを実行してマスターの登録状態を確認する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpdateConsentStatementRevision
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpdateConsentStatementRevision
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）ロールを持つユーザが実行できる。修正できる同意文書は、ユーザが所属する”company_id”（個人情報取扱事業者ID）と”organization_id”（組織ID）に対して登録された同意文書のみ。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>同意文書を修正する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>検索用の同意文書のメタデータを修正する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>更新対象の同意文書ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>group_company_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>version
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>バージョン番号、または公開日
   </td>
  </tr>
  <tr>
   <td>status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>公開状態（”Draft”もしくは”Published”のどちらか）。デフォルトは、”Draft”
<p>


<p id="gdcalert12" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: undefined internal link (link text: "同意文書のステータス"). Did you generate a TOC? </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert13">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>

<a href="#heading=h.u1fh6biqkow1">同意文書のステータス</a>
   </td>
  </tr>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書のタイトル
   </td>
  </tr>
  <tr>
   <td>abstract
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書の概要
   </td>
  </tr>
  <tr>
   <td>changes
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書の修正内容
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
  <tr>
   <td>consent_statement

   </td>
   <td>TEXT

   </td>
   <td>Y

   </td>
   <td>同意文書の本文（Markdown or HTML）

   </td>
  </tr>
  <tr>
   <td>optional_purposes

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>任意同意対象の利用目的

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的のタイトル
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>created_at

   </td>
   <td>BIGINT

   </td>
   <td>Y

   </td>
   <td>更新日時（ミリ秒）

   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新された同意文書IDである “hashed_asset_id” が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の同意文書ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”UUID”+"-"
<p>
 +”文書番号”
<p>
)
   </td>
  </tr>
</table>



### 同意文書の改訂（Contract）

既存の同意文書に対して改訂（再同意が必要な変更）を行う。改訂が成功すると、既存の同意文書とは別の新規登録された同意文書IDを返す。同意文書の改訂に当たっては、事前に参照されるマスター情報が登録されている必要があるが、別の✓ロジックを実行してマスターの登録状態を確認する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpdateConsentStatementVersion					
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpdateConsentStatementVersion					
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a					
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）ロールを持つユーザが実行できる。修正できる同意文書は、ユーザが所属する”company_id”（個人情報取扱事業者ID）と”organization_id”（組織ID）に対して登録された同意文書のみ。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>同意文書を修正する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>検索用の同意文書のメタデータを修正する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
  <tr>
   <td>asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td>asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>parent_consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>更新元となる同意文書の同意文書ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>group_company_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>version
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>バージョン番号、または公開日
   </td>
  </tr>
  <tr>
   <td>status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>同意文書の状態（”Draft”もしくは”Published”のどちらか）。デフォルトは、”Draft”
<p>


<p id="gdcalert13" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: undefined internal link (link text: "同意文書のステータス"). Did you generate a TOC? </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert14">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>

<a href="#heading=h.u1fh6biqkow1">同意文書のステータス</a>
   </td>
  </tr>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書のタイトル
   </td>
  </tr>
  <tr>
   <td>abstract
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書の概要
   </td>
  </tr>
  <tr>
   <td>changes
   </td>
   <td>
   </td>
   <td>
   </td>
   <td>同意文書の修正内容
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
  <tr>
   <td>consent_statement

   </td>
   <td>TEXT

   </td>
   <td>Y

   </td>
   <td>同意文書の本文（Markdown or HTML）

   </td>
  </tr>
  <tr>
   <td>optional_purposes

   </td>
   <td>Object

   </td>
   <td>
   </td>
   <td>任意同意対象の利用目的

   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>title
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的のタイトル
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>benefit_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>便益IDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>optional_third_parties
   </td>
   <td>Object
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意対象の第三者提供先に対する説明
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>利用期限ID

   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td>created_at

   </td>
   <td>BIGINT

   </td>
   <td>Y

   </td>
   <td>改訂日時（ミリ秒）

   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された同意文書IDである “hashed_asset_id” が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の同意文書ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”UUID”+"-"
<p>
 +”文書番号”
<p>
)
   </td>
  </tr>
</table>



### 同意文書の公開（Contract/Function）

既存の同意文書に対して、公開ステータスの更新を行う。公開ステータスの更新が成功すると、更新された同意文書IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpdateConsentStatementStatus
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpdateConsentStatementStatus
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）ロールを持つユーザが実行できる。公開ステータスの更新ができる同意文書は、ユーザが所属する”company_id”（個人情報取扱事業者ID）と”organization_id”（組織ID）に対して登録された同意文書のみ。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>同意文書を修正する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>検索用の同意文書のメタデータを修正する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>group_company_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>共同利用先事業者ID, 委託先事業者ID
   </td>
  </tr>
  <tr>
   <td>status
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>公開状態。
<p>


<p id="gdcalert14" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: undefined internal link (link text: "同意文書のステータス"). Did you generate a TOC? </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert15">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>

<a href="#heading=h.u1fh6biqkow1">同意文書のステータス</a>
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新したアセットのIDを返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>公開状態を変更したアセットのハッシュ化したIDを返す。
   </td>
  </tr>
</table>



### 同意文書の参照（Contract/Function）

既存の同意文書の内容を取得する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>GetConsentStatement
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

誰でも実行が可能。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>同意文書を参照する
   </td>
  </tr>
</table>



#### テーブル

n/a


#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>ハッシュ化した同意文書ID
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

取得が成功すると、取得した同意文書を返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>TEXT
   </td>
   <td>同意文書の本文（Markdown or HTML）
   </td>
  </tr>
  <tr>
   <td>version
   </td>
   <td>TEXT
   </td>
   <td>バージョン番号、または公開日
   </td>
  </tr>
  <tr>
   <td>changes
   </td>
   <td>TEXT
   </td>
   <td>変更内容
   </td>
  </tr>
  <tr>
   <td>abstract
   </td>
   <td>TEXT
   </td>
   <td>同意文書の概要
   </td>
  </tr>
  <tr>
   <td>purpose_ids
   </td>
   <td>Array
   </td>
   <td>利用目的IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_set_schema_ids
   </td>
   <td>Array
   </td>
   <td>データセットスキーマIDのリスト
   </td>
  </tr>
  <tr>
   <td>third_party_ids
   </td>
   <td>Array
   </td>
   <td>必須第三者提供先IDのリスト
   </td>
  </tr>
  <tr>
   <td>data_retention_policy_id
   </td>
   <td>TEXT
   </td>
   <td>利用期限ID
   </td>
  </tr>
</table>



## 同意（Consent）

同意は、同意文書に記載されている利用目的、データセットスキーマ、第三者提供先に対して、同意した項目、拒否した項目、利用期限で構成され、同意記録の更新コントラクトで操作を行う。


### 同意記録の更新

同意文書に対する同意項目、拒否項目、利用期限を登録する。登録に成功すると同意IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertConsentStatus
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Nane
   </td>
   <td>UpsertConsentStatus
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

データ主体が実行出来る。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent
   </td>
   <td>同意を登録、更新する
   </td>
  </tr>
  <tr>
   <td>consent_statement
   </td>
   <td>同意の対象となる同意文書の存在を確認する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>consent
   </td>
   <td>検索用の同意のメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
  <tr>
   <td>asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td>asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_statement_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>難読化された同意文書ID
   </td>
  </tr>
  <tr>
   <td colspan="4" >consent_status
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>同意文書への同意状態
<p>
["approved", "rejected", "configured"]
<p>
approved : 同意文書に含まれる全ての利用目的に同意している
<p>
rejected : 同意文書に含まれる全ての利用目的を拒否している
<p>
configured : 同意文書に含まれる利用目的の一部に同意している
   </td>
  </tr>
  <tr>
   <td colspan="4" >consented_detail
   </td>
   <td>
   </td>
   <td>
   </td>
   <td>同意した任意利用目的の内容
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >group_company_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>共同利用先企業ID, 委託先企業ID
   </td>
  </tr>
  <tr>
   <td colspan="3" >purpose_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="3" >dataset_schema_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="3" >benefit_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="3" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="3" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="3" >data_retention_policy
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Data Retention PolicyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="6" >

<table>
  <tr>
   <td colspan="2" >nondeletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>使用終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
  <tr>
   <td colspan="2" >deletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="3" >optional_purposes

   </td>
   <td>TEXT

   </td>
   <td>
   </td>
   <td>任意利用目的への同意内容JSON配列

   </td>
  </tr>
  <tr>
   <td colspan="6" >

<table>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>
   </td>
   <td>
   </td>
   <td>必須同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_retention_policy
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>Data Retention PolicyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="5" >

<table>
  <tr>
   <td>nondeletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>使用終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
  <tr>
   <td>deletion_purging
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>保持終了日
<p>
※フォーマットはアプリケーションの仕様に従う
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="4" >rejected_detail

   </td>
   <td>
   </td>
   <td>
   </td>
   <td>拒否した任意利用目的の内容

   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td>optional_third_party_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>任意同意のThird Partyアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td>optional_purposes
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意項目への同意状態JSON
   </td>
  </tr>
  <tr>
   <td colspan="4" >

<table>
  <tr>
   <td colspan="2" >purpose_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のPurposeアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >data_set_schema_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>必須同意のData Set Schemaアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >benefit_ids
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Benefitアセットのasset_ids
   </td>
  </tr>
  <tr>
   <td colspan="2" >third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>必須同意のThird PartyのJSON
   </td>
  </tr>
  <tr>
   <td colspan="2" >optional_third_party_ids
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>任意同意のThird PartyのJSON
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>


   </td>
  </tr>
  <tr>
   <td colspan="4" >updated_at

   </td>
   <td>BIGINT

   </td>
   <td>Y

   </td>
   <td>クライアントで取得したミリ秒単位の日時

   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された同意IDである “hashed_asset_id” が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の同意ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”同意文書ID"
<p>
 +”データ主体ID”
<p>
)
   </td>
  </tr>
</table>



## 利用目的（Purpose）


### 利用目的の登録

同意文書で同意を取得する際に提示するデータの利用目的を登録する。登録に成功すると利用目的IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>purpose
   </td>
   <td>利用目的を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>purpose
   </td>
   <td>検索用の利用目的のメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>category_of_purpose
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用目的の分類
<p>
TCF v2.0 などの規格名は、ここに格納する
   </td>
  </tr>
  <tr>
   <td>purpose_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用目的名
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用目的の説明
   </td>
  </tr>
  <tr>
   <td>legal_text
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>法的文書
   </td>
  </tr>
  <tr>
   <td>user_friendly_text
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>表示文書
   </td>
  </tr>
  <tr>
   <td>guidance
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用ガイド
   </td>
  </tr>
  <tr>
   <td>note
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>そのほかの補足事項
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された利用目的IDである “hashed_asset_id” が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の利用目的ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”UUID”+"-"
<p>
 +”利用目的番号”
<p>
)
   </td>
  </tr>
</table>



### 利用目的の活性状態を更新

事業者内で使用しなくなった利用目的を不活性化、または活性化する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>purpose
   </td>
   <td>利用目的の活性状態を更新する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>purpose
   </td>
   <td>検索用の利用目的のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>is_active
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>状態
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新したアセットのハッシュ化したIDを返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>活性状態を更新したアセットID
   </td>
  </tr>
</table>



## データセットスキーマ（Dataset Schema）


### データセットスキーマの登録

同意文書で同意を取得する際に提示するデータセットスキーマを登録する。登録に成功するとデータセットスキーマIDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>利用目的を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>検索用のデータセットスキーマのメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>data_set_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>データセット名
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>データセットの説明
   </td>
  </tr>
  <tr>
   <td>data_location
   </td>
   <td>JSON
   </td>
   <td>Y
   </td>
   <td>データの格納先情報
<p>
・アクセスパス
<p>
・接続手段
<p>
・認証手段
<p>
など
   </td>
  </tr>
  <tr>
   <td>category_of_data
   </td>
   <td>Array
   </td>
   <td>Y
   </td>
   <td>データの取扱区分
   </td>
  </tr>
  <tr>
   <td>data_type
   </td>
   <td>Array
   </td>
   <td>Y
   </td>
   <td>データの種類
   </td>
  </tr>
  <tr>
   <td>classification
   </td>
   <td>Array
   </td>
   <td>Y
   </td>
   <td>データの分類
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>JSON Schema
   </td>
   <td>Y
   </td>
   <td>データセットに含まれる項目を定義したJSON Schema
   </td>
  </tr>
  <tr>
   <td>changes
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>以下の変更履歴
<p>
・data_location に対する変更
<p>
・Active/Inactive に対する変更
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録されたデータセットスキーマIDである “hashed_asset_id” が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式のデータセットスキーマID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”UUID”+"-"
<p>
 +”データセットスキーマ番号”
<p>
)
   </td>
  </tr>
</table>



### データセットスキーマの活性状態を更新

事業者内で使用しなくなったデータセットスキーマを不活性化、または活性化する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>不活性化する利用目的を更新する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_set_schema
   </td>
   <td>検索用のデータセットスキーマのメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_ut
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新したアセットのハッシュ化したIDを返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>活性状態を更新したアセットID
   </td>
  </tr>
</table>



## 第三者提供先（Third Party）


### 第三者提供先の登録

同意文書で同意を取得する際に提示する、データを提供する第三者を登録する。登録に成功すると第三者IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>RegisterThirdParty
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>RegisterThirdParty
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Admin”（管理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>third_party
   </td>
   <td>第三者情報を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>third_party
   </td>
   <td>検索用の第三者情報のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
  <tr>
   <td>asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td>asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者ID
   </td>
  </tr>
  <tr>
   <td>third_party_domain
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>第三者提供先のドメイン名
   </td>
  </tr>
  <tr>
   <td>third_party_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>第三者提供先の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の国税庁の法人番号（オプショナル）
   </td>
  </tr>
  <tr>
   <td>third_party_metadata
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>第三者提供先の情報
   </td>
  </tr>
  <tr>
   <td>organizations
   </td>
   <td>Array
   </td>
   <td>Y
   </td>
   <td>第三者提供先内の組織。JSON Object構造を繰り返す。
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された第三者提供先IDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式のデータセットスキーマID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”事業者ID”+"-"
<p>
 +”第三者ID”
<p>
)
   </td>
  </tr>
</table>



### 第三者提供先の更新

同意文書で同意を取得する際に提示する、データを提供する第三者の情報を更新する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpdateThirdParty
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpdateThirdParty
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Admin”（管理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>third_party
   </td>
   <td>第三者情報を更新する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>third_party
   </td>
   <td>検索用の第三者情報のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolder ID
   </td>
  </tr>
  <tr>
   <td>asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td>asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者ID
   </td>
  </tr>
  <tr>
   <td>third_party_domain
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>第三者提供先のドメイン名
   </td>
  </tr>
  <tr>
   <td>third_party_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>第三者提供先の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>第三者提供先の国税庁の法人番号（オプショナル）
   </td>
  </tr>
  <tr>
   <td>third_party_metadata
   </td>
   <td>JSON
   </td>
   <td>Y
   </td>
   <td>第三者提供先の情報
   </td>
  </tr>
  <tr>
   <td>organizations
   </td>
   <td>Array
   </td>
   <td>Y
   </td>
   <td>第三者提供先内の組織。JSON Object構造を繰り返す。
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新したアセットのハッシュ化したIDを返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>活性状態を更新したアセットID
   </td>
  </tr>
</table>



## 便益（Benefit）


### 便益の登録

同意文書でデータ主体に対して提示するデータ主体に提供される便益を登録する。登録に成功すると便益IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>benefit
   </td>
   <td>便益を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>benefit
   </td>
   <td>検索用の便益のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>category_of_benefit
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>便益の分類
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>便益名
   </td>
  </tr>
  <tr>
   <td>benefit_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>便益の説明
   </td>
  </tr>
  <tr>
   <td>provider
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>便益の提供者
   </td>
  </tr>
  <tr>
   <td>tern_of_provide
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>便益の提供時期
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された便益IDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の便益ID。
<p>
Hashid(
<p>
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”UUID”+"-"
<p>
 +”便益番号”
<p>
)
   </td>
  </tr>
</table>



### 便益の活性状態を更新

事業者内で使用しなくなったデータセットスキーマを不活性化、または活性化する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>benefit
   </td>
   <td>不活性化する利用目的を更新する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>benefit
   </td>
   <td>検索用の利用目的のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新したアセットのハッシュ化したIDを返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>活性状態を更新したアセットID
   </td>
  </tr>
</table>



## 利用期限（Data Retention Policy）


### 利用期限の登録

同意文書で同意を取得する際に提示する利用期限を登録する。登録に成功すると、利用期限IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_retention_policy
   </td>
   <td>利用期限を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_retention_policy
   </td>
   <td>検索用の利用期限のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>policy_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用目的の分類
<p>
TCF v2.0 などの規格名は、ここに格納する
   </td>
  </tr>
  <tr>
   <td>policy_type
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用目的名
   </td>
  </tr>
  <tr>
   <td>length_of_use
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用期間
   </td>
  </tr>
  <tr>
   <td>length_of_retention
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>保持期間
   </td>
  </tr>
  <tr>
   <td>description
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>利用期限の説明
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された利用期限IDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式のデータセットスキーマID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”事業者ID”+"-"
<p>
 +”利用期限番号”
<p>
)
   </td>
  </tr>
</table>



### 利用期限の活性状態を更新

同意文書で同意を取得する際に提示する利用期限を不活性化、または活性化する。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertMaster
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

“Controller”（個人情報管理者）、“Processor”（情報処理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_retention_policy
   </td>
   <td>利用期限を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>data_retention_policy
   </td>
   <td>検索用の利用期限のメタデータを更新する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>AssetのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >table_schema
   </td>
   <td>Object
   </td>
   <td> Y
   </td>
   <td>TableのJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="7" >

<table>
  <tr>
   <td colspan="3" >table_name
   </td>
   <td>TEXT
   </td>
   <td> Y
   </td>
   <td>操作対象のテーブル名
   </td>
  </tr>
  <tr>
   <td colspan="3" >partition_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>PartitionKeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >clustering_keys
   </td>
   <td>Array(TEXT)
   </td>
   <td>
   </td>
   <td>Clustering KeyのKey名、データ型
   </td>
  </tr>
  <tr>
   <td colspan="3" >columns
   </td>
   <td>Array(TEXT)
   </td>
   <td> Y
   </td>
   <td>操作対象のTableの項目
   </td>
  </tr>
</table>


   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

更新が成功すると、更新したアセットのハッシュ化したIDを返す。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>活性状態を更新したアセットID
   </td>
  </tr>
</table>



## 事業者（Company）


### 事業者の登録

ISTで使用する事業者を登録する。登録に成功すると、事業者IDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>RegisterCompany
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>RegisterCompany
<p>
RegisterOrganization
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

”SysAdmin”（システム管理者）と”SysOperator”（システム運用管理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>company
   </td>
   <td>事業者を新規に追加する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>company
   </td>
   <td>検索用の事業者のメタデータを追加する
   </td>
  </tr>
  <tr>
   <td>organization
   </td>
   <td>検索用の組織のメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>executor_company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト実行者の個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>登録する個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_name	
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>国税庁の法人番号
   </td>
  </tr>
  <tr>
   <td>company_metadata
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者の情報
   </td>
  </tr>
  <tr>
   <td>organization_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>組織ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、登録された事業者IDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の事業者ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”個人情報取扱事業者ID”
<p>
)
   </td>
  </tr>
</table>



### 事業者の更新

既存の事業所の情報を更新する。更新が成功すると、更新された事業所IDを返す。更新可能な事業所の項目は、以下に示す項目である。



*   事業者の正式名称
*   国税庁の法人番号
*   事業者の情報

<table>
  <tr>
   <td>
Contract Name
   </td>
   <td>UpdateCompany					
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpdateCompany
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

”SysAdmin”（システム管理者）と”SysOperator”（システム運用管理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>company
   </td>
   <td>事業者を更新する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>company
   </td>
   <td>検索用の事業者のメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>executor_company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト実行者の個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>登録する個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_name	
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>国税庁の法人番号
   </td>
  </tr>
  <tr>
   <td>company_metadata
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者の情報
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、更新された事業者IDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の事業者ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”個人情報取扱事業者ID”
<p>
)
   </td>
  </tr>
</table>



### 組織の追加、更新

既存の事業所に所属する組織の情報を追加、更新する。追加、更新が成功すると、追加、または更新された事業所IDを返す。更新可能な組織の項目は、以下に示す項目である。



*   組織の名称
*   組織の説明

<table>
  <tr>
   <td>
Contract Name
   </td>
   <td>UpsertOrganization					
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertOrganization
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

”SysAdmin”（システム管理者）と”SysOperator”（システム運用管理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>company
   </td>
   <td>事業者を更新する
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>実行者のロール、所属組織を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>company
   </td>
   <td>検索用の事業者のメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>executor_company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト実行者の個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>登録する個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_name	
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者の正式名称
   </td>
  </tr>
  <tr>
   <td>corporate_number
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>国税庁の法人番号
   </td>
  </tr>
  <tr>
   <td>company_metadata
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>事業者の情報
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>updated_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>更新日時（ミリ秒）
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録が成功すると、更新された事業者IDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式の事業者ID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”個人情報取扱事業者ID”
<p>
)
   </td>
  </tr>
</table>



## ユーザープロファイル（User Profile）


### ユーザプロファイルの登録、更新

ユーザープロファイルの登録、更新を行う。登録が成功すると、登録、更新されたユーザープロファイルIDを返す。


<table>
  <tr>
   <td>Contract Name
   </td>
   <td>UpsertUserProfile					
   </td>
  </tr>
  <tr>
   <td>Contract Argument
   </td>
   <td>JSON Schema (see below)
   </td>
  </tr>
  <tr>
   <td>Function Name
   </td>
   <td>UpsertUserProfile
   </td>
  </tr>
  <tr>
   <td>Function Argument
   </td>
   <td>n/a
   </td>
  </tr>
  <tr>
   <td>Return
   </td>
   <td>hashed_asset_id
   </td>
  </tr>
</table>



#### 権限

”SysAdmin”（システム管理者）と”SysOperator”（システム運用管理者）と“Admin”（管理者）ロールを持つユーザーが実行できる。


#### アセット


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>ユーザープロファイルを登録、更新する
<p>
実行者のロール、所属事業者を参照する
   </td>
  </tr>
</table>



#### テーブル


<table>
  <tr>
   <td>アセット名
   </td>
   <td>操作内容
   </td>
  </tr>
  <tr>
   <td>user_profile
   </td>
   <td>検索用のユーザープロファイルのメタデータを追加する
   </td>
  </tr>
</table>



#### Contract Properties


<table>
  <tr>
   <td colspan="4" >項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td colspan="4" >holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト登録者のHolderID
   </td>
  </tr>
  <tr>
   <td colspan="4" >contract_argument_schema
   </td>
   <td>Object
   </td>
   <td>Y
   </td>
   <td>Contract Argument で受け渡されるJSONスキーマ情報
   </td>
  </tr>
  <tr>
   <td colspan="4" >company_asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するCompanyアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >company_asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するCompanyアセットのバージョン
   </td>
  </tr>
  <tr>
   <td colspan="4" >user_profile_asset_name
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクトが操作するUser Profileアセット名
   </td>
  </tr>
  <tr>
   <td colspan="4" >user_profile_asset_version
   </td>
   <td>TEXT
   </td>
   <td>
   </td>
   <td>コントラクトが操作するUser Profileアセットのバージョン
   </td>
  </tr>
</table>



#### Contract Argument


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>必須
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>executor_company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>コントラクト実行者の個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>company_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>登録する個人情報取扱事業者ID
   </td>
  </tr>
  <tr>
   <td>organization_ids
   </td>
   <td>Array
   </td>
   <td>Y
   </td>
   <td>ユーザーが所属する組織IDの配列
   </td>
  </tr>
  <tr>
   <td>roles
   </td>
   <td>Array
   </td>
   <td>
   </td>
   <td>ユーザーに付与するロールの配列
   </td>
  </tr>
  <tr>
   <td>holder_id
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>ユーザーのHolder ID
   </td>
  </tr>
  <tr>
   <td>created_at
   </td>
   <td>BIGINT
   </td>
   <td>Y
   </td>
   <td>作成日時（ミリ秒）
   </td>
  </tr>
  <tr>
   <td>mode
   </td>
   <td>TEXT
   </td>
   <td>Y
   </td>
   <td>ユーザープロファイルの登録方法
   </td>
  </tr>
</table>



#### Function Argument

n/a


#### Return

登録、更新が成功すると、登録、更新されたユーザープロファイルIDである "hashed_asset_id" が返される。


<table>
  <tr>
   <td>項目名
   </td>
   <td>データ型
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>hashed_asset_id
   </td>
   <td>TEXT
   </td>
   <td>以下の形式のデータセットスキーマID。
<p>
Hashid( \
   properties.asset_name
<p>
 + properties.asset_version
<p>
 +"-"
<p>
 +”事業者ID”+"-"
<p>
 +”Holder ID”
<p>
)
   </td>
  </tr>
</table>



## Appendix


#### 同意文書のステータス


<table>
  <tr>
   <td><strong>項目</strong>
   </td>
   <td><strong>内容</strong>
   </td>
  </tr>
  <tr>
   <td>draft
   </td>
   <td>下書き文書
   </td>
  </tr>
  <tr>
   <td>reviewed
   </td>
   <td>レビュー済みだが未公開の文書
   </td>
  </tr>
  <tr>
   <td>published
   </td>
   <td>公開済み文書
   </td>
  </tr>
  <tr>
   <td>inactive
   </td>
   <td>無効化した公開済み文書
   </td>
  </tr>
</table>



#### レスポンスフォーマット

コントラクト実行時は、以下のフォーマットをもつJSONを返却する。


<table>
  <tr>
   <td colspan="2" >項目名
   </td>
   <td colspan="2" >説明
   </td>
  </tr>
  <tr>
   <td colspan="2" >response
   </td>
   <td colspan="2" >コントラクトからの返却値
   </td>
  </tr>
  <tr>
   <td colspan="2" >error_message
   </td>
   <td colspan="2" >コントラクト内での例外発生時に設定されるメッセージ
   </td>
  </tr>
  <tr>
   <td rowspan="6" >
   </td>
   <td>domain
   </td>
   <td colspan="2" >例外が発生したコントラクト名、またはファンクション名
   </td>
  </tr>
  <tr>
   <td>code
   </td>
   <td colspan="2" >

<p id="gdcalert15" ><span style="color: red; font-weight: bold">>>>>>  gd2md-html alert: undefined internal link (link text: "エラーコード"). Did you generate a TOC? </span><br>(<a href="#">Back to top</a>)(<a href="#gdcalert16">Next alert</a>)<br><span style="color: red; font-weight: bold">>>>>> </span></p>

<a href="#heading=h.49ifda8xoo8h">エラーコード</a>
   </td>
  </tr>
  <tr>
   <td>message
   </td>
   <td colspan="2" >エラーの詳細
   </td>
  </tr>
  <tr>
   <td>contract_argument
   </td>
   <td colspan="2" >コントラクトに渡されたArgument
<p>
※デバッグモードONの時のみ出力
   </td>
  </tr>
  <tr>
   <td>function_argument
   </td>
   <td colspan="2" >ファンクションに渡されたArgument
<p>
※ファンクションで例外発生時のみ出力
<p>
※デバッグモードONの時のみ出力
   </td>
  </tr>
  <tr>
   <td>stack_trace
   </td>
   <td colspan="2" >エラー発生時のスタックトレース
<p>
※デバッグモードONの時のみ出力
   </td>
  </tr>
</table>



#### エラーコード表


<table>
  <tr>
   <td>コード
   </td>
   <td>メッセージ
   </td>
   <td>説明
   </td>
  </tr>
  <tr>
   <td>INVALID_CONTRACT_ARGUMENTS
   </td>
   <td>There is an error while validating the arguments.
   </td>
   <td>指定したContract Argument が、JSON Schema定義に適合していない
   </td>
  </tr>
  <tr>
   <td>INVALID_FUNCTION_ARGUMENTS
   </td>
   <td>There is an error while validating the arguments.
   </td>
   <td>指定したFunction Argument が、JSON Schema定義に適合していない
   </td>
  </tr>
  <tr>
   <td>CONTRACT_ARGUMENT_SCHEMA_IS_MISSING
   </td>
   <td>Contract argument schema is not specified in the properties.
   </td>
   <td>Contract PropertiesにJSON Schema定義がない
   </td>
  </tr>
  <tr>
   <td>HOLDER_ID_IS_MISSING
   </td>
   <td>Executor's holder ID is missing in the contract properties of the specified contract.
   </td>
   <td>Contract PropertiesにHolder ID定義がない
   </td>
  </tr>
  <tr>
   <td>HOLDER_ID_IS_NOT_MATCHED
   </td>
   <td>Executor's holder ID does not match with the one specified in the properties.
   </td>
   <td>Contract PropertiesのHolder IDとContract ArgumentのHolder IDが一致しない
   </td>
  </tr>
  <tr>
   <td>INVALID_CONTRACT_ARGUMENTS_SCHEMA
   </td>
   <td>The schema provided is not valid.
   </td>
   <td>JSON Schema定義が不正なフォーマット
   </td>
  </tr>
  <tr>
   <td>REQUIRED_ATTRIBUTES_FOR_USER_PROFILE_ARE_MISSING
   </td>
   <td>The specified user profile has missing attributes of roles or organization_ids
   </td>
   <td>User Profileに必須の属性（Role, Organization_id）が存在しない
   </td>
  </tr>
  <tr>
   <td>INVALID_ORGANIZATION_SPECIFIED
   </td>
   <td>The specified argument organization ids does not match with the company asset organization ids
   </td>
   <td>指定した組織IDが個人情報取扱事業者に存在しない
   </td>
  </tr>
  <tr>
   <td>PERMISSION_DENIED
   </td>
   <td>Permission is not granted due to inadequate roles or organization ids provided.
   </td>
   <td>実行者が実行に必要なRoleを持っていない
<p>
または、実行者の所属する個人情報取扱事業者、組織権限がない
   </td>
  </tr>
  <tr>
   <td>DISALLOWED_CONTRACT_EXECUTION_ORDER
   </td>
   <td>The contract is not allowed to execute in the specified order.
   </td>
   <td>直接実行が許可されていないコントラクトの実行を行った
   </td>
  </tr>
  <tr>
   <td>EXECUTOR_COMPANY_ID_DOES_NOT_MATCH_WITH_USER_PROFILE_COMPANY_ID
   </td>
   <td>The specified executor company id does not match with the user profile company id.
   </td>
   <td>実行者の個人情報取扱事業者IDとユーザープロファイルの個人情報取扱事業者IDが一致しない
   </td>
  </tr>
  <tr>
   <td>ASSET_IS_ALREADY_REGISTERED
   </td>
   <td>Asset provided is already registered in the database.
   </td>
   <td>アセット登録時に同じアセットIDがすでに存在している
   </td>
  </tr>
  <tr>
   <td>RECORD_IS_ALREADY_REGISTERED
   </td>
   <td>Record provided is already registered in the database.
   </td>
   <td>レコード登録時に同じPartition Key、Clustering Keyのレコードがすでに存在している
   </td>
  </tr>
  <tr>
   <td>ASSET_NOT_FOUND
   </td>
   <td>Asset is not found in the ledger.
   </td>
   <td>指定されたアセットIDに該当するアセットが存在しない
   </td>
  </tr>
  <tr>
   <td>RECORD_NOT_FOUND
   </td>
   <td>Record is not found in the database.
   </td>
   <td>指定したPartition Key、Clustering Keyのレコードが存在しない
   </td>
  </tr>
  <tr>
   <td>SALT_IS_MISSING
   </td>
   <td>Salt is not found in the properties.
   </td>
   <td>Contract Propertiesの必須項目であるsaltが存在しない
   </td>
  </tr>
  <tr>
   <td>REQUIRED_CONTRACT_PROPERTIES_ARE_MISSING
   </td>
   <td>Contract properties are not provided.
   </td>
   <td>Contract Propertiesが登録されていない
   </td>
  </tr>
  <tr>
   <td>ORGANIZATION_ALREADY_IN_USE
   </td>
   <td>The organization is already tied to a company.
   </td>
   <td>指定した組織IDはすでに使われている
   </td>
  </tr>
</table>


正常終了時のレスポンス例


```
{
  "hashed_asset_id":
"d71g69rEWMfbWD4PE5pNhvlRn6DPnAsBKopBvy5MIEAnAQLRXbu64DzvDvaJhrXY4LboPwFXV7O6VrDMFNgJNLn2gqS1v"
}
```


エラー発生時のレスポンス例


```
{
  "error_message":{
    "domain":"com.scalar.ist.contract.ValidateArgument",
    "code":"INVALID_CONTRACT_ARGUMENTS",
    "message":"There is an error while validating the arguments.",
    "contract_argument":{
      "action":"insert",
      "company_id":"scalar-labs.com",
      "organization_id":"9ca84f95-2e84-4707-8206-b93c9e78d7b7",
      "category_of_purpose":"category sed anim",
      "purpose_name":"purpose_name dolore magnaxxx",
      "description":"description",
      "legal_text":"legal_text",
      "user_friendly_text":"user_friendly_text",
      "guidance":"guidance",
      "note":"note",
      "created_at":1572514828169,
      "_functions_":[
        "UpsertMaster"
      ]
    },
    "function_argument":{},
    "stack_trace":[
      "at com.scalar.ist.contract.ValidateArgument.validate(ValidateArgument.java:61)",
      "at com.scalar.ist.contract.ValidateArgument.invoke(ValidateArgument.java:30)",
      "at com.scalar.dl.ledger.contract.Contract.invoke(Contract.java:89)",
      "at com.scalar.ist.contract.UpsertMaster.invokeSubContract(UpsertMaster.java:231)",
      "at com.scalar.ist.contract.UpsertMaster.validateArguments(UpsertMaster.java:138)",
      "at com.scalar.ist.contract.UpsertMaster.validate(UpsertMaster.java:106)",
      "at com.scalar.ist.contract.UpsertMaster.invoke(UpsertMaster.java:72)",
      "at com.scalar.dl.ledger.contract.ContractExecutor.invoke(ContractExecutor.java:75)",
      "at com.scalar.dl.ledger.contract.ContractExecutor.execute(ContractExecutor.java:46)",
      "at com.scalar.dl.ledger.service.LedgerService.executeContract(LedgerService.java:119)",
      "at com.scalar.dl.ledger.service.LedgerService.execute(LedgerService.java:95)",
      "at com.scalar.dl.ledger.server.LedgerService.executeContract(LedgerService.java:110)",
      "at com.scalar.dl.rpc.LedgerGrpc$MethodHandlers.invoke(LedgerGrpc.java:487)",
      "at io.grpc.stub.ServerCalls$UnaryServerCallHandler$UnaryServerCallListener.onHalfClose(ServerCalls.java:171)",
      "at io.grpc.internal.ServerCallImpl$ServerStreamListenerImpl.halfClosed(ServerCallImpl.java:283)",
      "at io.grpc.internal.ServerImpl$JumpToApplicationThreadServerStreamListener$1HalfClosed.runInContext(ServerImpl.java:707)",
      "at io.grpc.internal.ContextRunnable.run(ContextRunnable.java:37)",
      "at io.grpc.internal.SerializingExecutor.run(SerializingExecutor.java:123)",
      "at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)",
      "at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)",
      "at java.lang.Thread.run(Thread.java:748)"
    ]
  }
}
```



#### 個人情報取扱事業者のロール一覧


<table>
  <tr>
   <td><strong>カテゴリ</strong>
   </td>
   <td><strong>ロール名</strong>
   </td>
   <td><strong>英語</strong>
   </td>
   <td><strong>説明</strong>
   </td>
  </tr>
  <tr>
   <td rowspan="6" >個人情報保護
   </td>
   <td>データ主体
   </td>
   <td>Data Subject
   </td>
   <td>情報の持ち主（情報の主体）。本人の情報の提供と同意を行う
   </td>
  </tr>
  <tr>
   <td>データ提供者
   </td>
   <td>Provisioner
   </td>
   <td>データ主体の依頼を受けて、本人に代わってデータを提供する。※同意は行えない
   </td>
  </tr>
  <tr>
   <td>情報管理者
   </td>
   <td>Controller
   </td>
   <td>個人データの処理の目的および手段を決定する
   </td>
  </tr>
  <tr>
   <td>情報処理者
   </td>
   <td>Processor
   </td>
   <td>個人データを処理する
   </td>
  </tr>
  <tr>
   <td>データ保護責任者
   </td>
   <td>Controller
   </td>
   <td>情報管理者もしくは情報処理者が個人データ保護指針を遵守できているかの監視等を行う
   </td>
  </tr>
  <tr>
   <td>データ受領者
   </td>
   <td>Processor
   </td>
   <td>第三者としてデータを受領する
   </td>
  </tr>
  <tr>
   <td rowspan="3" >システム利用事業者
   </td>
   <td>管理者
   </td>
   <td>Admin
   </td>
   <td>自事業者のアカウントを登録・削除を行う。
   </td>
  </tr>
  <tr>
   <td>システム運用者
   </td>
   <td>Operator
   </td>
   <td>他システムへの接続を行う。
   </td>
  </tr>
  <tr>
   <td>メンバー
   </td>
   <td>Member
   </td>
   <td>システムの自身のアカウントの登録・更新・削除を行う
   </td>
  </tr>
  <tr>
   <td rowspan="2" >システム運用事業者
   </td>
   <td>システム管理者
   </td>
   <td>SysAdmin
   </td>
   <td>システム利用事業者の登録、更新、削除、システム利用事業者のアカウント管理者の登録、更新、削除等のシステム管理全般の業務を行う
   </td>
  </tr>
  <tr>
   <td>システム運用者
   </td>
   <td>SysOperator
   </td>
   <td>システム起動・停止、アダプター開発・適用等、システム全般に係るオペレーションを行う
   </td>
  </tr>
</table>


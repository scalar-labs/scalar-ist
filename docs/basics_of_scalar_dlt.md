# Scalar DLTの用語、および実行コマンドの説明
## Holder / Certificate
Holderは、コントラクトの実行を行うユーザーに相当し、Holder IDはScalar DLTのクラスタ内で一意でとなる必要があります。

CertificateはVersion毎に管理しており、1つのHolderが複数のバージョンの証明書を登録する事が出来ます。

## Contract
Scalar DLTで実行するコントラクトは、証明書ごとに登録、実行を行うことができます。

コントラクトはID、Contract Name、Byte Codeから構成され、IDは [Holder ID] / [Certificate Version] / [コントラクト登録時のID] で構成され、一意である必要があります。

コントラクト登録時は、実行者の[Holder ID]、[Certificate Version]、登録するコントラクトの [コントラクト登録時のID]、Contract Name、Byte Code、Contract Propertiesを指定します。

コントラクト実行時は、実行者の[Holder ID]、[Certificate Version]、実行するコントラクトの [コントラクト登録時のID]、コントラクト引数、ファンクション引数（Optional） を指定します。

## Function
Scalar DLTで実行するファンクションは全てのHolderで共通のファンクションを使用します。

ファンクションはID、Function Name、Byte Codeから構成され、ファンクションIDは一意である必要があります。

ファンクションの実行は、コントラクト引数に実行するファンクションIDを指定することで、コントラクト実行時にファンクションがInvokeされます。

ファンクションIDは複数指定することが出来るが、ファンクションの実行順番は保証されないため、順序性が必要な処理をファンクションで実行する場合は、1つのファンクション内で実装する必要があります。

## 秘密鍵、証明書を作成
openssl / cfsslを使った秘密鍵、証明書手順は、以下のサイトの記載しています。

https://scalardl.readthedocs.io/en/latest/ca/caclient-getting-started/#generate-a-private-key-and-a-csr


## 証明書の登録、コントラクトの登録、コントラクトの実行
証明書の登録、コントラクトの登録、コントラクト実行手順は、以下のサイトの記載しています。

https://scalardl.readthedocs.io/en/latest/getting-started/

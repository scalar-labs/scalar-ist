# Infrastructure
# 利用手順

ISTのコントラクト/ファンクションはScalar DLT環境で動作するため、Scalar DLT環境が必要です。
Scalar DLT環境の作成には、Dockerを使った簡易な方法と、Scalar Terraformを使ったクラウドサービスでの環境構築の方法があります。

## 環境構築方法

## 事前準備
環境構築時に使用するDockerイメージは、Docker-HubのPrivate Repositoryに格納しているため、使用のためにはユーザーへの権限付与が必要です。

権限付与については弊社までお問い合わせ下さい。

### Dockerを使った環境構築

Scalar DLTのDockerイメージを使用したScalar DLT環境の構築を行います。

Dockerイメージを使った環境構築手順は以下に記載しています。
https://scalardl.readthedocs.io/en/latest/installation-with-docker/



### Scalar Terraformを使った環境構築

Terraformを使用しクラウドサービス上でのScalar DLT環境の構築を行います。
Terraformのスクリプトは以下のリポジトリに格納しています。

https://github.com/scalar-labs/scalar-terraform


HTTPServerをJavaで書いて見る
======================
TG社の課題

使い方
------
* ./gradlew runで実行できます。

仕様
-----
* サーバーのポート番号は8080です。

* URIで「/」と指定することでディレクトリ内のウェルカムページ(index.html)が呼ばれます。

* 「/program/board/」にアクセスすると掲示板システムが利用できます。

* リクエストを同時に処理はできません。先にきたリクエストのレスポンスを返し終わると、次のリクエストの処理へ移ります。

* ファイルの文字コードはUTF-8を使用してください。

[![CircleCI](https://circleci.com/gh/asada0701/MyWebServer.svg?style=svg)](https://circleci.com/gh/asada0701/MyWebServer)

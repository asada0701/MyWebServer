HTTPServerをJavaで書いて見る
======================
TG社の課題

使い方
------
./gradlew runで実行後、

1:サーバーを立ち上げます。

2:サーバーを停止します。

3:プログラムを終了します。

1,2,3のいずれかの数字を入力してください。

仕様
-----
* サーバーのポート番号は8080です。

* URIで「/」と指定することでディレクトリ内のindex.htmlが呼ばれます。

* リクエストを同時に処理はできません。先にきたリクエストのレスポンスを返し終わると、次のリクエストの処理へ移ります。

* ファイルの文字コードはUTF-8を使用してください。

* urlのパターンの注意点
  * /program/board/」と「/program/」は同じ扱いとなります。どちらのパスが優先されるかの保証はできません。
  * /program3/board/」と「/program/board/」は別扱いとなります。

[![CircleCI](https://circleci.com/gh/asada0701/MyWebServer.svg?style=svg)](https://circleci.com/gh/asada0701/MyWebServer)

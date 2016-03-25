# CountServer

CounterServer(デフォルトはport:23400)にメッセージを送信すると、直近のワードカウントランキングを出力する

## Usage

* 起動方法  
mvn compile exec:java -Dstorm.topology=storm.hot.Topology  

* オプション：  
-Dwidth.slide: 集計結果の出力頻度(秒)、デフォルトは60（毎分出力）  
-Dwidth.Window: 現在からの集計対象時間(秒)、デフォルトは3600（直近1時間） 

* tools/Rankings.javaで出力先を設定

* メッセージ送信方法  
ソケット通信でポート23400にメッセージを投げてください  
HotWordSpout.javaでポートとかは設定できます

## UserDictionary

* resourses/dic.csvはユーザ辞書
* SplitBolt.javaあたりを見てもらうと読み込むユーザ辞書の設定が可能



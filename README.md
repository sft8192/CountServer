# CountServer

localhost:23400にメッセージを送信すると、直近のワードカウントランキングを出力する

## Usage

* 起動方法  
mvn compile exec:java -Dstorm.topology=storm.hot.Topology  

* オプション：  
-Dwidth.slide: 集計結果の出力頻度(秒)、デフォルトは60（毎分出力）  
-Dwidth.Window: 現在からの集計対象時間(秒)、デフォルトは3600（直近1時間） 

* tools/Rankings.javaで出力先を設定

## UserDictionary

* resourses/dic.csvはユーザ辞書
* SplitBolt.javaあたりを見てもらうと読み込むユーザ辞書の設定が可能



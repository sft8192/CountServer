package storm.hot;
import java.util.HashMap;
import java.util.Map;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
//import it.sauronsoftware.cron4j.Scheduler;

public class WordCountBolt extends BaseBasicBolt{
    Map<String, Integer> counts = new HashMap<String, Integer>();
	public void execute(Tuple tuple, BasicOutputCollector collector) {


		final BasicOutputCollector collect = collector;

	      String word = tuple.getString(0);
	      Integer count = counts.get(word);
	      if (count == null)
	        count = 0;

	      count++;
	      counts.put(word, count);

	      collector.emit(new Values(word, count));
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	      declarer.declare(new Fields("word", "count"));

	}

}

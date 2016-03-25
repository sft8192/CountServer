package storm.hot;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;
import org.atilika.kuromoji.Tokenizer.Builder;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SplitBolt extends BaseBasicBolt{

	public void execute(Tuple tuple, BasicOutputCollector collector) {

		Builder builder = Tokenizer.builder();
		Tokenizer tokenizer = builder.build();

		String sentence = tuple.getString(0);
		List<Token> tokens = tokenizer.tokenize(sentence);

		for (Token token : tokens) {
			String[] features = token.getAllFeaturesArray();
			if(((features[0].equals("名詞") &&features[1].equals("一般"))|| features[1].equals("固有名詞"))){
				if(!features[1].equals("except")){
				collector.emit(new Values(token.getSurfaceForm()));
				}
			}
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	      declarer.declare(new Fields("word"));
	}

    @Override
    public Map<String, Object> getComponentConfiguration() {
      return null;
    }
}

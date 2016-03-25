package storm.hot;

import backtype.storm.Config;
import backtype.storm.testing.TestWordSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import storm.hot.util.StormRunner;

/**
 * This topology does a continuous computation of the top N words that the topology has seen in terms of cardinality.
 * The top N computation is done in a completely scalable way, and a similar approach could be used to compute things
 * like trending topics or trending images on Twitter.
 */
public class RollingTopWords {

  private static final int DEFAULT_RUNTIME_IN_SECONDS = 60;
  private static final int TOP_N = 10;

  private final TopologyBuilder builder;
  private final String topologyName;
  private final Config topologyConfig;
  private final int runtimeInSeconds;

  public RollingTopWords() throws InterruptedException {
    builder = new TopologyBuilder();
    topologyName = "slidingWindowCounts";
    topologyConfig = createTopologyConfiguration();
    runtimeInSeconds = DEFAULT_RUNTIME_IN_SECONDS;

    wireTopology();
  }

  private static Config createTopologyConfiguration() {
    Config conf = new Config();
    conf.setDebug(true);
    return conf;
  }

  private void wireTopology() throws InterruptedException {
    String spoutId = "wordGenerator";
    String splitId = "split";
    String counterId = "counter";
    String intermediateRankerId = "intermediateRanker";
    String totalRankerId = "finalRanker";

    builder.setSpout(spoutId, new HotWordSpout(), 1);
    builder.setBolt(splitId, new SplitBolt(), 6).shuffleGrouping(spoutId);
    builder.setBolt(counterId, new RollingCountBolt(9, 3), 6).fieldsGrouping(splitId, new Fields("word"));
    builder.setBolt(intermediateRankerId, new IntermediateRankingsBolt(TOP_N), 4).fieldsGrouping(counterId, new Fields("obj"));
    builder.setBolt(totalRankerId, new TotalRankingsBolt(TOP_N)).globalGrouping(intermediateRankerId);
  }

  public void run() throws InterruptedException {
    StormRunner.runTopologyLocally(builder.createTopology(), topologyName, topologyConfig, runtimeInSeconds);
  }

  public static void main(String[] args) throws Exception {
    new RollingTopWords().run();
  }
}
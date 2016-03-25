package storm.hot;

import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@SuppressWarnings("serial")
	public class HotWordSpout extends BaseRichSpout {

	private SpoutOutputCollector collector;
	private ServerSocket serverSocket; // サーバ用のソケット
	private Socket socket = null; // ソケットをやり取りする為に使用する
	private BufferedReader in = null;
	private String str;

	public HotWordSpout() {
	}

	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {

		this.collector = collector;

	}

		public void nextTuple() {

			ExecutorService worker = Executors.newCachedThreadPool();
			try (ServerSocket listener = new ServerSocket();) {
				listener.setReuseAddress(true);
				listener.bind(new InetSocketAddress(23400));
				while (true) {
					final Socket socket = listener.accept();
					worker.submit(new Runnable() {
						@Override
						public void run() {
							try {
								InputStream from = socket.getInputStream();
								BufferedReader in = new BufferedReader(new InputStreamReader(from));
								str = in.readLine(); // クライアント側からの送信を１行読み込む
								collector.emit(new Values(str));
							} catch (IOException e) {
								e.printStackTrace();
							} finally {
								try {
									socket.close();
								} catch (IOException e) {
								}
							}
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				worker.shutdown();
			}

		}

		@Override
		public void close() {
		}

		@Override
		public Map<String, Object> getComponentConfiguration() {
			Config ret = new Config();
			ret.setMaxTaskParallelism(1);
			return ret;
		}

		@Override
		public void ack(Object id) {
		}

		@Override
		public void fail(Object id) {
		}

		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}

	}

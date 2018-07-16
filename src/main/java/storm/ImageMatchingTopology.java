package storm;

import com.julianovidal.storm.redis.spout.RedisSpout;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import storm.bolt.ImageMatchingBolt;

public class ImageMatchingTopology {
	
	private static final String TOPOLOGY_NAME = "redis_spout_topology_test";
	
	public StormTopology buildTopology() {
		
		TopologyBuilder builder = new TopologyBuilder();
		
		builder.setSpout("spout1", new RedisSpout(1, "input", "msg"), 1);
		
		builder.setBolt("bolt1", new ImageMatchingBolt()).noneGrouping("spout1");
		
		return builder.createTopology();
		
	}
	
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		
		ImageMatchingTopology topologyStrategy = new ImageMatchingTopology();
		
		Config conf = new Config();
		conf.setNumWorkers(1);
		
		conf.put(Config.TOPOLOGY_RECEIVER_BUFFER_SIZE,            8);
		conf.put(Config.TOPOLOGY_TRANSFER_BUFFER_SIZE,            32);
		conf.put(Config.TOPOLOGY_EXECUTOR_SEND_BUFFER_SIZE,       16384);
		conf.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE,    16384);
		
		conf.put(Config.STORM_MESSAGING_NETTY_BUFFER_SIZE,        16384);
		
		conf.put("redisHost", "localhost");
		// conf.put("redisPort", 6379);
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology(TOPOLOGY_NAME, conf, topologyStrategy.buildTopology());
	}

}


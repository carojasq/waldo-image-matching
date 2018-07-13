package storm.bolt;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.julianovidal.storm.redis.connector.RedisConnector;
import imageprocessing.TemplateMatching;
import imageprocessing.Utils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageMatchingBolt extends BaseBasicBolt {

	private static final long serialVersionUID = -667521342792637217L;

	public static Logger LOG = LoggerFactory.getLogger(ImageMatchingBolt.class);

	protected TemplateMatching matcher;
	protected Jedis jedis;


	public void putResultsInRedis(String requestId, Map<String,String> results) {
		JSONObject resultsJSon = new JSONObject();
		resultsJSon.putAll(results);
		this.jedis.set(requestId, resultsJSon.toJSONString());
		LOG.info(String.format("Request %s, %s", requestId, results));
	}

	@Override
	public void prepare(Map conf, TopologyContext context) {
		super.prepare(conf, context);

		this.matcher =  new TemplateMatching(1, 0.65);

		// Prepare jedis to store output
		String redisHost = conf.containsKey("redisHost") ? (String) conf.get("redisHost") : "localhost";
		Integer redisPort = conf.containsKey("redisPort") ? Integer.parseInt(conf.get("redisPort").toString()) : 6379;
		LOG.info(String.format("Connecting to redis with params %s:%s", redisHost, redisPort.toString()));
		RedisConnector redisConnector = new RedisConnector(redisHost, redisPort);
		this.jedis = redisConnector.getResource();
	}


	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		Object value = input.getValue(0);

		if(value instanceof List) {
			for (String msg :  (List<String>) value) {
				Map<String, String> matchResult;

				String[] parts = msg.split(",");
				if (parts.length==3) {

					String file1, file2 = null;
					try {
						LOG.info(String.format("Part1 %s part2 %s part3 %s", parts[0].length(), parts[1].length(), parts[2].length()));
						file1 = Utils.base64ImgToTmpFile(parts[0]);
						file2 = Utils.base64ImgToTmpFile(parts[1]);
						matchResult = matcher.isSubImage(file1, file2);
					} catch (Exception e) {
						matchResult =  new HashMap<>();
						matchResult.put("match", "f");
						matchResult.put("x", "-1");
						matchResult.put("y", "-1");
						LOG.error("Exception matching the images");
						LOG.error(e.getMessage());
					}
					String uuid = parts[2];
					putResultsInRedis(uuid, matchResult);

					// TemplateMatching.isSubImage(file1, file27, "nothiiing", 1);
				} else {
					LOG.info("Not valid message");
				}

			}

		}
	}
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
	}
}
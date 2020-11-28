package tu.cit.kafka.examples;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class HelloStream {
    private static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        //Step 1 :  Create Configuration
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG,AppConfigs.applicationID);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,AppConfigs.bootstrapServers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,Serdes.String().getClass());

        //Step 2 : Create Stream Topology
        //Step2.1: Create KStream to read the record from source topic
        StreamsBuilder streamsBuilder = new StreamsBuilder();
        KStream kStream = streamsBuilder.stream(AppConfigs.topicName);

        //Step2.2: Process the KStream.It means apply business logic
        kStream.foreach((k,v)->System.out.println("Key : " + k + ", Value : "+ v));

        //Step2.3: Create topology to wrap all KStream object and business login in one object.
        Topology topology = streamsBuilder.build();

        //Step 3 : Start the stream
        KafkaStreams kafkaStreams = new KafkaStreams(topology,props);
        logger.info("Stream is starting...");
        kafkaStreams.start();

        //Step 4 : Add shutdownhook to shutdown gracefully.
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            logger.info("Stream is shutting down...");
            kafkaStreams.close();
        }));

    }
}

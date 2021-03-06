package com.maxplus1;

import com.maxplus1.demo.storm.props.StormProps;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//@ImportResource("classpath:spring/beans.xml")
public class DemoApplication {

    public static void main(String[] args)
            throws InvalidTopologyException, AuthorizationException, AlreadyAliveException, InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        StormProps stormProps = context.getBean(StormProps.class);
        TopologyBuilder topologyBuilder = context.getBean(TopologyBuilder.class);
        Config config = new Config();
        remoteSubmit(stormProps,topologyBuilder,config);
//        localSubmit(stormProps.getTopologyName(),topologyBuilder,config);
//        context.close();
        SpringApplication.exit(context);
    }

    private static void remoteSubmit(StormProps stormProps,TopologyBuilder builder, Config conf)
            throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        conf.setNumWorkers(stormProps.getTopologyWorkers());
        conf.setMaxSpoutPending(stormProps.getTopologyMaxSpoutPending());
        StormSubmitter.submitTopology(stormProps.getTopologyName(), conf, builder.createTopology());
    }

    /**
     * 用于debug
     * @param name
     * @param builder
     * @throws InterruptedException
     */
    private static void localSubmit(String name,TopologyBuilder builder, Config conf)
            throws InterruptedException {
        conf.setDebug(true);
        conf.setMaxTaskParallelism(3);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology(name, conf, builder.createTopology());
        Thread.sleep(100000);
        cluster.shutdown();
    }
}

// package org.onosproject.ngsdn.tutorial;

// import com.google.common.collect.Lists;
// import org.onlab.util.SharedScheduledExecutors;
// import org.onosproject.cfg.ComponentConfigService;
// import org.onosproject.core.ApplicationId;
// import org.onosproject.core.CoreService;
// import org.onosproject.net.Device;
// import org.onosproject.net.DeviceId;
// import org.onosproject.net.config.ConfigFactory;
// import org.onosproject.net.config.NetworkConfigRegistry;
// import org.onosproject.net.config.basics.SubjectFactories;
// import org.onosproject.net.device.DeviceService;
// import org.onosproject.net.flow.FlowRule;
// import org.onosproject.net.flow.FlowRuleService;
// import org.onosproject.net.group.Group;
// import org.onosproject.net.group.GroupService;
// import org.osgi.service.component.annotations.Activate;
// import org.osgi.service.component.annotations.Component;
// import org.osgi.service.component.annotations.Deactivate;
// import org.osgi.service.component.annotations.Reference;
// import org.osgi.service.component.annotations.ReferenceCardinality;
// import org.onosproject.ngsdn.tutorial.common.FabricDeviceConfig;
// import org.onosproject.ngsdn.tutorial.pipeconf.PipeconfLoader;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.util.Collection;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;
// import org.onosproject.net.flow.*;
// import org.onosproject.net.packet.*;
// import org.onosproject.net.*;
// import static org.onosproject.ngsdn.tutorial.AppConstants.APP_NAME;
// import static org.onosproject.ngsdn.tutorial.AppConstants.CLEAN_UP_DELAY;
// import static org.onosproject.ngsdn.tutorial.AppConstants.DEFAULT_CLEAN_UP_RETRY_TIMES;
// import static org.onosproject.ngsdn.tutorial.common.Utils.sleep;
// import org.onosproject.ngsdn.tutorial.common.Utils;

// import org.onosproject.net.pi.model.PiTableId;
// import org.onosproject.net.pi.model.PiMatchFieldId;
// import org.onlab.packet.MacAddress;
// import org.onlab.packet.Ethernet;
// //import org.onlab.packet.Ipv6Address;
// import org.onlab.packet.TCP;
// import org.onlab.packet.UDP;
// import org.onlab.packet.IPv6;
// import org.onlab.packet.ICMP6;
// //import org.onlab.packet.IpProtocol;

// import java.nio.ByteBuffer;

// import org.onosproject.net.pi.model.PiActionId;
// import org.onosproject.net.pi.model.PiActionParamId;
// import org.onosproject.net.pi.model.PiMatchFieldId;
// import org.onosproject.net.pi.runtime.PiAction;
// import org.onosproject.net.pi.runtime.PiActionParam;
// import org.onosproject.net.pi.runtime.PiActionProfileGroupId;
// import org.onosproject.net.pi.runtime.PiTableAction;
// import org.onosproject.net.flow.criteria.PiCriterion;
// /**
//  * A component which among other things registers the fabricDeviceConfig to the
//  * netcfg subsystem.
//  */
// @Component(immediate = true, service = MainComponent.class)
// public class MainComponent {

//     private static final Logger log =
//             LoggerFactory.getLogger(MainComponent.class.getName());

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     private CoreService coreService;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     //Force activation of this component after the pipeconf has been registered.
//     @SuppressWarnings("unused")
//     protected PipeconfLoader pipeconfLoader;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     protected NetworkConfigRegistry configRegistry;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     private GroupService groupService;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     private DeviceService deviceService;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     private FlowRuleService flowRuleService;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     private ComponentConfigService compCfgService;

//     @Reference(cardinality = ReferenceCardinality.MANDATORY)
//     private PacketService packetService;

//     private final ConfigFactory<DeviceId, FabricDeviceConfig> fabricConfigFactory =
//             new ConfigFactory<DeviceId, FabricDeviceConfig>(
//                     SubjectFactories.DEVICE_SUBJECT_FACTORY, FabricDeviceConfig.class, FabricDeviceConfig.CONFIG_KEY) {
//                 @Override
//                 public FabricDeviceConfig createConfig() {
//                     return new FabricDeviceConfig();
//                 }
//             };

//     private ApplicationId appId;
//     private PacketProcessor processor = new DdosPacketProcessor();
//     // For the sake of simplicity and to facilitate reading logs, use a
//     // single-thread executor to serialize all configuration tasks.
//     private final ExecutorService executorService = Executors.newSingleThreadExecutor();

//     @Activate
//     protected void activate() {
//         appId = coreService.registerApplication(APP_NAME);

//         // Wait to remove flow and groups from previous executions.
//         waitPreviousCleanup();

//         compCfgService.preSetProperty("org.onosproject.net.flow.impl.FlowRuleManager",
//                                       "fallbackFlowPollFrequency", "4", false);
//         compCfgService.preSetProperty("org.onosproject.net.group.impl.GroupManager",
//                                       "fallbackGroupPollFrequency", "3", false);
//         compCfgService.preSetProperty("org.onosproject.provider.host.impl.HostLocationProvider",
//                                       "requestIpv6ND", "true", false);
//         compCfgService.preSetProperty("org.onosproject.provider.lldp.impl.LldpLinkProvider",
//                                       "useBddp", "false", false);


//         configRegistry.registerConfigFactory(fabricConfigFactory);
//         //ddos logic
//         packetService.addProcessor(processor, PacketProcessor.director(2));
//         requestIntercepts();

//         log.info("Started");
//     }

//     @Deactivate
//     protected void deactivate() {

//         withdrawIntercepts();
//         packetService.removeProcessor(processor);

//         configRegistry.unregisterConfigFactory(fabricConfigFactory);

//         cleanUp();

//         log.info("Stopped");
//     }

//     /**
//      * Returns the application ID.
//      *
//      * @return application ID
//      */
//     ApplicationId getAppId() {
//         return appId;
//     }

//     /**
//      * Returns the executor service managed by this component.
//      *
//      * @return executor service
//      */
//     public ExecutorService getExecutorService() {
//         return executorService;
//     }

//     /**
//      * Schedules a task for the future using the executor service managed by
//      * this component.
//      *
//      * @param task task runnable
//      * @param delaySeconds delay in seconds
//      */
//     public void scheduleTask(Runnable task, int delaySeconds) {
//         SharedScheduledExecutors.newTimeout(
//                 () -> executorService.execute(task),
//                 delaySeconds, TimeUnit.SECONDS);
//     }

//     /**
//      * Triggers clean up of flows and groups from this app, returns false if no
//      * flows or groups were found, true otherwise.
//      *
//      * @return false if no flows or groups were found, true otherwise
//      */
//     private boolean cleanUp() {
//         Collection<FlowRule> flows = Lists.newArrayList(
//                 flowRuleService.getFlowEntriesById(appId).iterator());

//         Collection<Group> groups = Lists.newArrayList();
//         for (Device device : deviceService.getAvailableDevices()) {
//             groupService.getGroups(device.id(), appId).forEach(groups::add);
//         }

//         if (flows.isEmpty() && groups.isEmpty()) {
//             return false;
//         }

//         flows.forEach(flowRuleService::removeFlowRules);
//         if (!groups.isEmpty()) {
//             // Wait for flows to be removed in case those depend on groups.
//             sleep(1000);
//             groups.forEach(g -> groupService.removeGroup(
//                     g.deviceId(), g.appCookie(), g.appId()));
//         }

//         return true;
//     }

//     private void waitPreviousCleanup() {
//         int retry = DEFAULT_CLEAN_UP_RETRY_TIMES;
//         while (retry != 0) {

//             if (!cleanUp()) {
//                 return;
//             }

//             log.info("Waiting to remove flows and groups from " +
//                              "previous execution of {}...",
//                      appId.name());

//             sleep(CLEAN_UP_DELAY);

//             --retry;
//         }
//     }




//     private void requestIntercepts() {
//         TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
//         packetService.requestPackets(selector.build(), PacketPriority.REACTIVE, appId);
//     }

//     private void withdrawIntercepts() {
//         TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
//         packetService.cancelPackets(selector.build(), PacketPriority.REACTIVE, appId);
//     }
//     private class DdosPacketProcessor implements PacketProcessor {
//         @Override
//         public void process(PacketContext context) {
//             if (context.isHandled()) {
//                 return;
//             }
    
//             InboundPacket pkt = context.inPacket();
//             Ethernet ethPkt = pkt.parsed();
//             if (ethPkt == null) {
//                 return;
//             }
//             ConnectPoint connectPoint = pkt.receivedFrom();
//             log.info("Packet received from port: {}", connectPoint.port());


//             if (ethPkt.getEtherType() == Ethernet.TYPE_IPV6) {
//                 IPv6 ipv6Packet = (IPv6) ethPkt.getPayload();
//                 byte nextHeader = ipv6Packet.getNextHeader();
                
//                 if (nextHeader == IPv6.PROTOCOL_TCP) {
//                     TCP tcpPacket = (TCP) ipv6Packet.getPayload();
//                     int dstPort = tcpPacket.getDestinationPort();
//                     log.info("TCP Destination Port: {}", dstPort);
//                 } else if (nextHeader == IPv6.PROTOCOL_UDP) {
//                     UDP udpPacket = (UDP) ipv6Packet.getPayload();
//                     int dstPort = udpPacket.getDestinationPort();
//                     log.info("UDP Destination Port: {}", dstPort);
//                 } else if (nextHeader == IPv6.PROTOCOL_ICMP6) {
//                     ICMP6 icmp6Packet = (ICMP6) ipv6Packet.getPayload();
//                     log.info("ICMPv6 Packet Type: {}", icmp6Packet.getIcmpType());
//                 }
//                 else {
//                     log.info("Unsupported IPv6 next header: {}", nextHeader);
//                 }
//             }
//             else {log.info("not ipv6 packet");}



//             log.info("Ethernet packet: {}",ethPkt.toString());
//             log.info("Source MAC: {}", ethPkt.getSourceMAC());
//             log.info("InboundPacket received from: {}", context.inPacket().receivedFrom());
//             log.info("Packet data: {}", context.inPacket().unparsed());
//             // 解析数据包头部中的特征
//             ByteBuffer payload = ByteBuffer.wrap(ethPkt.serialize());
//             // 假设特征在头部的前20个字节内
//             byte[] features = new byte[20];
//             payload.get(features, 0, 20);
//             StringBuilder sb = new StringBuilder();
//                 for (byte b : features) {
//                 sb.append(String.format("%02X ", b));
//             }   
//             log.info("Features: {}", sb.toString());
//             // 调用机器学习模型进行判断
//             //boolean isDdos = classifyPacket(features);
//             //isDdos = true;
//             //if (true) passPacket(context);
//             log.info("yzy6666666");
//             //blockPacket(context);
//             //blockPacket_drop(context);
//             log.info("dou drop lalalalla");
//             // } else {
//             //     passPacket(context);
//             // }
//         }
//     }
    
//     private boolean classifyPacket(byte[] features) {
//         try {
//             // 假设我们有一个本地Python脚本或API可以调用来进行判断
//             ProcessBuilder pb = new ProcessBuilder("python3", "ml_model.py", new String(features));
//             Process p = pb.start();
//             p.waitFor();
//             BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//             String result = reader.readLine();
//             return Boolean.parseBoolean(result);
//         } catch (Exception e) {
//             log.error("Error calling ML model: ", e);
//             return false;
//         }
//     }
    
//     private void blockPacket(PacketContext context) {
//         // 下发流表规则以阻止该数据包
//         // TrafficTreatment dropTreatment = DefaultTrafficTreatment.builder().drop().build();
//         InboundPacket pkt = context.inPacket();
//         Ethernet ethPkt = pkt.parsed();
       
//         if (ethPkt == null) {
//             return; // 如果数据包为空或无法解析，则直接返回
//         }
//         MacAddress srcmac = ethPkt.getSourceMAC();
        
//         DeviceId deviceId = context.inPacket().receivedFrom().deviceId();
//         log.info("Adding Dangerous MAC rules to {}...", deviceId);
//         final String dangerous_mac_tableid_1 = "IngressPipeImpl.dangerous_mac_table_1";
//         final String dangerous_mac_tableid_2 = "IngressPipeImpl.dangerous_mac_table_2";
//         final PiCriterion match = PiCriterion.builder()
//         .matchExact(
//                 PiMatchFieldId.of("hdr.ethernet.src_addr"),
//                 srcmac.toBytes())
//         .build();

//         final PiTableAction action = PiAction.builder()
//         .withId(PiActionId.of("IngressPipeImpl.drop"))
//         .build();
//         //下发规则到dangerous_mac_table_1
//         final FlowRule dangerous_mac_table_1 = Utils.buildFlowRule(
//             deviceId, appId, dangerous_mac_tableid_1, match, action);
//         log.info("deviceId, appId, tableId, match, action", deviceId, appId, dangerous_mac_tableid_1, match, action);
//         flowRuleService.applyFlowRules(dangerous_mac_table_1);
//         //下发规则到dangerous_mac_table_2
//         final FlowRule dangerous_mac_table_2 = Utils.buildFlowRule(
//             deviceId, appId, dangerous_mac_tableid_2, match, action);
//         log.info("deviceId, appId, tableId, match, action", deviceId, appId, dangerous_mac_tableid_2, match, action);
//         flowRuleService.applyFlowRules(dangerous_mac_table_2);
//     }
//     private void blockPacket_drop(PacketContext context) {
//         context.block();
//     }
    
//     private void passPacket(PacketContext context) {
//         // 允许数据包通过
//         // context.treatmentBuilder().build();
//         // context.send();
//         if (context == null) {
//             log.error("PacketContext is null");
//             return;
//         }
//         if (context.outPacket() == null) {
//             log.error("OutboundPacket is null");
//             return;
//         }
//         PortNumber outPort = context.outPacket().inPort();
//         if (outPort == null) {
//             log.warn("inPort is null, using default port");
//             outPort = context.inPacket().receivedFrom().port();
//         }
//         TrafficTreatment treatment = DefaultTrafficTreatment.builder().setOutput(outPort).build();
//         OutboundPacket outboundPacket = new DefaultOutboundPacket(
//                 context.inPacket().receivedFrom().deviceId(),
//                 treatment,
//                 context.inPacket().unparsed()
//         );
//         packetService.emit(outboundPacket);
//         log.info("pass!");
//     }

// }
package org.onosproject.ngsdn.tutorial;

import com.google.common.collect.Lists;
import org.onlab.util.SharedScheduledExecutors;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.onosproject.net.config.basics.SubjectFactories;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.group.Group;
import org.onosproject.net.group.GroupService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.onosproject.ngsdn.tutorial.common.FabricDeviceConfig;
import org.onosproject.ngsdn.tutorial.pipeconf.PipeconfLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.onosproject.ngsdn.tutorial.AppConstants.APP_NAME;
import static org.onosproject.ngsdn.tutorial.AppConstants.CLEAN_UP_DELAY;
import static org.onosproject.ngsdn.tutorial.AppConstants.DEFAULT_CLEAN_UP_RETRY_TIMES;
import static org.onosproject.ngsdn.tutorial.common.Utils.sleep;

/**
 * A component which among other things registers the fabricDeviceConfig to the
 * netcfg subsystem.
 */
@Component(immediate = true, service = MainComponent.class)
public class MainComponent {

    private static final Logger log =
            LoggerFactory.getLogger(MainComponent.class.getName());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    //Force activation of this component after the pipeconf has been registered.
    @SuppressWarnings("unused")
    protected PipeconfLoader pipeconfLoader;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected NetworkConfigRegistry configRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private GroupService groupService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private ComponentConfigService compCfgService;

    private final ConfigFactory<DeviceId, FabricDeviceConfig> fabricConfigFactory =
            new ConfigFactory<DeviceId, FabricDeviceConfig>(
                    SubjectFactories.DEVICE_SUBJECT_FACTORY, FabricDeviceConfig.class, FabricDeviceConfig.CONFIG_KEY) {
                @Override
                public FabricDeviceConfig createConfig() {
                    return new FabricDeviceConfig();
                }
            };

    private ApplicationId appId;

    // For the sake of simplicity and to facilitate reading logs, use a
    // single-thread executor to serialize all configuration tasks.
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Activate
    protected void activate() {
        appId = coreService.registerApplication(APP_NAME);

        // Wait to remove flow and groups from previous executions.
        waitPreviousCleanup();

        compCfgService.preSetProperty("org.onosproject.net.flow.impl.FlowRuleManager",
                                      "fallbackFlowPollFrequency", "4", false);
        compCfgService.preSetProperty("org.onosproject.net.group.impl.GroupManager",
                                      "fallbackGroupPollFrequency", "3", false);
        compCfgService.preSetProperty("org.onosproject.provider.host.impl.HostLocationProvider",
                                      "requestIpv6ND", "true", false);
        compCfgService.preSetProperty("org.onosproject.provider.lldp.impl.LldpLinkProvider",
                                      "useBddp", "false", false);

        configRegistry.registerConfigFactory(fabricConfigFactory);
        log.info("Started");
    }

    @Deactivate
    protected void deactivate() {
        configRegistry.unregisterConfigFactory(fabricConfigFactory);

        cleanUp();

        log.info("Stopped");
    }

    /**
     * Returns the application ID.
     *
     * @return application ID
     */
    ApplicationId getAppId() {
        return appId;
    }

    /**
     * Returns the executor service managed by this component.
     *
     * @return executor service
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Schedules a task for the future using the executor service managed by
     * this component.
     *
     * @param task task runnable
     * @param delaySeconds delay in seconds
     */
    public void scheduleTask(Runnable task, int delaySeconds) {
        SharedScheduledExecutors.newTimeout(
                () -> executorService.execute(task),
                delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * Triggers clean up of flows and groups from this app, returns false if no
     * flows or groups were found, true otherwise.
     *
     * @return false if no flows or groups were found, true otherwise
     */
    private boolean cleanUp() {
        Collection<FlowRule> flows = Lists.newArrayList(
                flowRuleService.getFlowEntriesById(appId).iterator());

        Collection<Group> groups = Lists.newArrayList();
        for (Device device : deviceService.getAvailableDevices()) {
            groupService.getGroups(device.id(), appId).forEach(groups::add);
        }

        if (flows.isEmpty() && groups.isEmpty()) {
            return false;
        }

        flows.forEach(flowRuleService::removeFlowRules);
        if (!groups.isEmpty()) {
            // Wait for flows to be removed in case those depend on groups.
            sleep(1000);
            groups.forEach(g -> groupService.removeGroup(
                    g.deviceId(), g.appCookie(), g.appId()));
        }

        return true;
    }

    private void waitPreviousCleanup() {
        int retry = DEFAULT_CLEAN_UP_RETRY_TIMES;
        while (retry != 0) {

            if (!cleanUp()) {
                return;
            }

            log.info("Waiting to remove flows and groups from " +
                             "previous execution of {}...",
                     appId.name());

            sleep(CLEAN_UP_DELAY);

            --retry;
        }
    }
}

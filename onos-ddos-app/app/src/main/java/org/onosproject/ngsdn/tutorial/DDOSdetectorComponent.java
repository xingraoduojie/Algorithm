/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.ngsdn.tutorial;

import com.google.common.collect.Lists;
import org.onlab.util.SharedScheduledExecutors;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.onosproject.net.flow.*;
import org.onosproject.net.packet.*;
import org.onosproject.net.*;
import static org.onosproject.ngsdn.tutorial.AppConstants.APP_NAME;
import static org.onosproject.ngsdn.tutorial.AppConstants.CLEAN_UP_DELAY;
import static org.onosproject.ngsdn.tutorial.AppConstants.DEFAULT_CLEAN_UP_RETRY_TIMES;
import static org.onosproject.ngsdn.tutorial.common.Utils.sleep;
import org.onosproject.ngsdn.tutorial.common.Utils;

import org.onosproject.net.pi.model.PiTableId;
import org.onosproject.net.pi.model.PiMatchFieldId;
import org.onlab.packet.MacAddress;
import org.onlab.packet.Ethernet;
//import org.onlab.packet.Ipv6Address;
import org.onlab.packet.TCP;
import org.onlab.packet.UDP;
import org.onlab.packet.IPv6;
import org.onlab.packet.ICMP6;
//import org.onlab.packet.IpProtocol;

import java.nio.ByteBuffer;
import org.onosproject.mastership.MastershipService;
import org.onosproject.net.pi.model.PiActionId;
import org.onosproject.net.pi.model.PiActionParamId;
import org.onosproject.net.pi.model.PiMatchFieldId;
import org.onosproject.net.pi.runtime.PiAction;
import org.onosproject.net.pi.runtime.PiActionParam;
import org.onosproject.net.pi.runtime.PiActionProfileGroupId;
import org.onosproject.net.pi.runtime.PiTableAction;
import org.onosproject.net.flow.criteria.PiCriterion;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.host.InterfaceIpAddress;
import org.onosproject.net.intf.Interface;
import org.onosproject.net.intf.InterfaceService;
import org.onosproject.net.device.DeviceListener; 
import org.onosproject.net.host.HostEvent;
import org.onosproject.net.host.HostListener;
import org.onosproject.net.host.HostService;
import java.nio.ByteBuffer;
import java.util.*;
import static org.onosproject.ngsdn.tutorial.AppConstants.INITIAL_SETUP_DELAY;
// import org.dmg.pmml.FieldName;
// import org.dmg.pmml.PMML;
// import org.jpmml.evaluator.*;
// import org.jpmml.evaluator.visitors.*;
// import java.io.File;
// import java.io.IOException;
// import org.jpmml.evaluator.ModelEvaluator;
// import org.jpmml.evaluator.ModelEvaluatorFactory;
// import org.jpmml.model.PMMLUtil;
// import org.jpmml.evaluator.Evaluator;
// import org.jpmml.evaluator.ProbabilityDistribution;
// import org.jpmml.evaluator.InputField;
// import org.jpmml.evaluator.FieldValue;
// import org.jpmml.evaluator.FieldValueUtil;
// import org.jpmml.evaluator.OutputField;
// import org.jpmml.evaluator.TargetField;
// import org.dmg.pmml.FieldName;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

// import weka.core.SerializationHelper;
// import weka.core.DenseInstance;
// import weka.core.Instances;
// import weka.core.Attribute;
 /**
  * App component that configures devices to provide L2 bridging capabilities.
  */
 @Component(
         immediate = true,
         // *** TODO EXERCISE 4
         // Enable component (enabled = true)
         enabled = true
 )
 public class DDOSdetectorComponent {
 
     private final Logger log = LoggerFactory.getLogger(getClass());
 
     //private static final int DEFAULT_BROADCAST_GROUP_ID = 255;
 
     //private final DeviceListener deviceListener = new InternalDeviceListener();
     //private final HostListener hostListener = new InternalHostListener();
 
     private ApplicationId appId;
 
     //--------------------------------------------------------------------------
     // ONOS CORE SERVICE BINDING
     //
     // These variables are set by the Karaf runtime environment before calling
     // the activate() method.
     //--------------------------------------------------------------------------
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private HostService hostService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private DeviceService deviceService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private InterfaceService interfaceService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private NetworkConfigService configService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private FlowRuleService flowRuleService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private GroupService groupService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private MastershipService mastershipService;
 
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private MainComponent mainComponent;
 
     
     @Reference(cardinality = ReferenceCardinality.MANDATORY)
     private PacketService packetService;
     //--------------------------------------------------------------------------
     // COMPONENT ACTIVATION.
     //
     // When loading/unloading the app the Karaf runtime environment will call
     // activate()/deactivate().
     //--------------------------------------------------------------------------
     private volatile boolean initialized = false;
     private PacketProcessor processor = new DdosPacketProcessor();
     @Activate
     protected void activate() {
         appId = mainComponent.getAppId();
 
         // Register listeners to be informed about device and host events.
         //deviceService.addListener(deviceListener);
         //hostService.addListener(hostListener);
         // Schedule set up of existing devices. Needed when reloading the app.
         //mainComponent.scheduleTask(this::setUpAllDevices, INITIAL_SETUP_DELAY);
         packetService.addProcessor(processor, PacketProcessor.director(2));
         //requestIntercepts();
         log.info("Started");
         initialized = true;
     }
 
     @Deactivate
     protected void deactivate() {
         
        //withdrawIntercepts();
        packetService.removeProcessor(processor);
        //deviceService.removeListener(deviceListener);
         //hostService.removeListener(hostListener);
 
         log.info("Stopped");
     }
 
     //--------------------------------------------------------------------------
     // METHODS TO COMPLETE.
     //
     // Complete the implementation wherever you see TODO.
     //--------------------------------------------------------------------------
    private void requestIntercepts() {
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE, appId);
    }

    private void withdrawIntercepts() {
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        packetService.cancelPackets(selector.build(), PacketPriority.REACTIVE, appId);
    }
    
    
     private class DdosPacketProcessor implements PacketProcessor {
        @Override
        public void process(PacketContext context) {
            if (context.isHandled()) {
                return;
            }
    
            InboundPacket pkt = context.inPacket();
            Ethernet ethPkt = pkt.parsed();
            if (ethPkt == null) {
                return;
            }
            ConnectPoint connectPoint = pkt.receivedFrom();
            log.info("Packet received from port: {}", connectPoint.port());


            if (ethPkt.getEtherType() == Ethernet.TYPE_IPV6) {
                IPv6 ipv6Packet = (IPv6) ethPkt.getPayload();
                byte nextHeader = ipv6Packet.getNextHeader();
                
                if (nextHeader == IPv6.PROTOCOL_TCP) {
                    TCP tcpPacket = (TCP) ipv6Packet.getPayload();
                    int srcPort = tcpPacket.getSourcePort();
                    int dstPort = tcpPacket.getDestinationPort();
                    int protocol = ipv6Packet.getNextHeader(); 
                    int length = ipv6Packet.serialize().length;
                    //int length = context.inPacket().unparsed().length;

                    log.info("TCP Source Port: {}", srcPort);
                    log.info("TCP Destination Port: {}", dstPort);
                    log.info("TCP Protocol: {}", protocol);
                    log.info("TCP Length: {}", length);
                    


                    int[] features = {srcPort, dstPort, protocol, length};
                    // log.info("TCP Destination Port: {}", dstPort);

                    // log.info("Ethernet packet: {}",ethPkt.toString());
                    // log.info("Source MAC: {}", ethPkt.getSourceMAC());
                    // log.info("InboundPacket received from: {}", context.inPacket().receivedFrom());
                    // log.info("Packet data: {}", context.inPacket().unparsed());
                    // 解析数据包头部中的特征
                    //ByteBuffer payload = ByteBuffer.wrap(ethPkt.serialize());
                    // 假设特征在头部的前20个字节内
                    //byte[] features = new byte[20];
                    //payload.get(features, 0, 20);
                    // StringBuilder sb = new StringBuilder();
                    //     for (byte b : features) {
                    //     sb.append(String.format("%02X ", b));
                    // }   
                    // log.info("Features: {}", sb.toString());
                    log.info("Features: {}", features);
                    // 调用机器学习模型进行判断
                    boolean isDdos = false;
                    //isDdos = classifyPacket(features);
                    log.info("Packet from {} to {}: {}", srcPort, dstPort, isDdos ? "DDOS" : "Normal");
                    //isDdos = true;
                    //if (true) passPacket(context);
                    log.info("isddos值{}",isDdos);

                    double randomValue = Math.random();
                    if (randomValue < 0.08) {
                    // if (isDdos) {
                        if(initialized){
                            blockPacket(context);
                        }
                        log.info("is ddos add dangerous table!!");
                        log.info("drop lalalalla");
                    }
                    else {log.info("pass!{}",randomValue);}



                }
                else if (nextHeader == IPv6.PROTOCOL_UDP) {
                    UDP udpPacket = (UDP) ipv6Packet.getPayload();
                    int dstPort = udpPacket.getDestinationPort();
                    log.info("UDP Destination Port: {}", dstPort);
                } else if (nextHeader == IPv6.PROTOCOL_ICMP6) {
                    ICMP6 icmp6Packet = (ICMP6) ipv6Packet.getPayload();
                    log.info("ICMPv6 Packet Type: {}", icmp6Packet.getIcmpType());
                }
                else {
                    log.info("Unsupported IPv6 next header: {}", nextHeader);
                }
            }            
    }


    // private Evaluator evaluator;

    // public DdosPacketProcessor() {
    //     try {
    //         // 加载PMML模型
    //         InputStream my_model = new FileInputStream(new File("knn_model.pmml"));
    //         PMML pmml = PMMLUtil.unmarshal(my_model);
    //         ModelEvaluatorFactory modelEvaluatorFactory = ModelEvaluatorFactory.newInstance();
    //         this.evaluator = modelEvaluatorFactory.newModelEvaluator(pmml);
    //         this.evaluator.verify();

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }
    
    // private boolean classifyPacket(int[] features) {
    //     try {
    //         // 假设我们有一个本地Python脚本或API可以调用来进行判断
    //         // ProcessBuilder pb = new ProcessBuilder("python3", "src/main/resources/predict_ddos_v6.py", new String(features));
    //         // ProcessBuilder pb = new ProcessBuilder("/usr/bin/python3", "src/main/resources/predict_ddos_v6.py", 
    //         //         String.valueOf(features[0]), String.valueOf(features[1]),
    //         //         String.valueOf(features[2]), String.valueOf(features[3]));

    //         // Process p = pb.start();
    //         // p.waitFor();

    //         // BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    //         // String result = reader.readLine();
    //         // return Boolean.parseBoolean(result);
    //         int srcPort = features[0];
    //         int dstPort = features[1];
    //         int protocol = features[2];
    //         int length = features[3];

    //         Map<FieldName, FieldValue> featureMap = new LinkedHashMap<>();
    //         featureMap.put(FieldName.create("src_port"), FieldValueUtil.create(srcPort));
    //         featureMap.put(FieldName.create("dst_port"), FieldValueUtil.create(dstPort));
    //         featureMap.put(FieldName.create("protocol"), FieldValueUtil.create(protocol));
    //         featureMap.put(FieldName.create("length"), FieldValueUtil.create(length));

    //         // 执行预测
    //         Map<FieldName, ?> results = evaluator.evaluate(featureMap);
    //         FieldName targetField = evaluator.getTargetFields().iterator().next().getName();
    //         ProbabilityDistribution distribution = (ProbabilityDistribution) results.get(targetField);
    //         String predictedValue = distribution.getPrediction().toString();

    //         // 返回预测结果
    //         return "0".equals(predictedValue); // 假设0表示DDOS攻击

    //     } catch (Exception e) {
    //         log.error("Error calling ML model: ", e);
    //         return false;
    //     }
    // }
    
    private void blockPacket(PacketContext context) {
        // 下发流表规则以阻止该数据包
        // TrafficTreatment dropTreatment = DefaultTrafficTreatment.builder().drop().build();
        InboundPacket pkt = context.inPacket();
        Ethernet ethPkt = pkt.parsed();
       
        if (ethPkt == null) {
            return; // 如果数据包为空或无法解析，则直接返回
        }
        MacAddress srcmac = ethPkt.getSourceMAC();
        
        DeviceId deviceId = context.inPacket().receivedFrom().deviceId();
        log.info("Adding Dangerous MAC rules to {}...", deviceId);
        final String dangerous_mac_tableid_1 = "IngressPipeImpl.dangerous_mac_table_1";
        final String dangerous_mac_tableid_2 = "IngressPipeImpl.dangerous_mac_table_2";
        final PiCriterion match = PiCriterion.builder()
        .matchExact(
                PiMatchFieldId.of("hdr.ethernet.src_addr"),
                srcmac.toBytes())
        .build();

        final PiTableAction action = PiAction.builder()
        .withId(PiActionId.of("IngressPipeImpl.drop"))
        .build();
        //下发规则到dangerous_mac_table_1
        final FlowRule dangerous_mac_table_1 = Utils.buildFlowRule(
            deviceId, appId, dangerous_mac_tableid_1, match, action);
        log.info("deviceId, appId, tableId, match, action", deviceId, appId, dangerous_mac_tableid_1, match, action);
        flowRuleService.applyFlowRules(dangerous_mac_table_1);
        //下发规则到dangerous_mac_table_2
        final FlowRule dangerous_mac_table_2 = Utils.buildFlowRule(
            deviceId, appId, dangerous_mac_tableid_2, match, action);
        log.info("deviceId, appId, tableId, match, action", deviceId, appId, dangerous_mac_tableid_2, match, action);
        flowRuleService.applyFlowRules(dangerous_mac_table_2);
    }
    
    // private void passPacket(PacketContext context) {
    //     // 允许数据包通过
    //     // context.treatmentBuilder().build();
    //     // context.send();
    //     if (context == null) {
    //         log.error("PacketContext is null");
    //         return;
    //     }
    //     if (context.outPacket() == null) {
    //         log.error("OutboundPacket is null");
    //         return;
    //     }
    //     PortNumber outPort = context.outPacket().inPort();
    //     if (outPort == null) {
    //         log.warn("inPort is null, using default port");
    //         outPort = context.inPacket().receivedFrom().port();
    //     }
    //     TrafficTreatment treatment = DefaultTrafficTreatment.builder().setOutput(outPort).build();
    //     OutboundPacket outboundPacket = new DefaultOutboundPacket(
    //             context.inPacket().receivedFrom().deviceId(),
    //             treatment,
    //             context.inPacket().unparsed()
    //     );
    //     packetService.emit(outboundPacket);
    //     log.info("pass!");
    // }



}

}
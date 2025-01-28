
#!/usr/bin/env python3

# # import random
# # import sys
# # from scapy.all import IPv6, TCP, send,RandIP6
# # def flood_ipv6_tcp(target_ip, target_port, count):
# #     for _ in range(count):
# #         # 构建IPv6和TCP层
# #         #如果需要随机ipv6源地址的情况
# #         src_ip = RandIP6()
# #         #ipv6 = IPv6(src=src_ip,dst=target_ip)
# #         #不需要随机生成ipv6源地址的情况
# #         ipv6 = IPv6(dst=target_ip)
# #         #print(f"source_ip:", src_ip)
# #         tcp = TCP(dport=target_port, sport=random.randint(49152, 49200))
# #         #tcp = TCP(dport=target_port)
# #         # 构建完整数据包
# #         packet = ipv6 / tcp
# #         # 发送数据包
# #         send(packet, verbose=False)
# #         print(f"Sent {_+1}/{count} IPv6 TCP packet to {target_ip}:{target_port}")

# # if __name__ == "__main__":
# #     # if len(sys.argv) != 4:
# #     #     print("Usage: python3 script.py <target_ip> <target_port> <packet_count>")
# #     #     sys.exit(1)

# #     # target_ip = sys.argv[1]
# #     #target_ip = "2001:0002:0001:0000:0000:0000:0000:0001"
# #     target_ip = "2001:2:1::1"
# #     # target_port = int(sys.argv[2])
# #     target_port = 80
# #     # packet_count = int(sys.argv[3])
# #     packet_count = 60

# #     flood_ipv6_tcp(target_ip, target_port, packet_count)
# #     print("Flooding completed.")

# import random
# from datetime import datetime
# from scapy.all import *

# def flood_ipv6_tcp(target_ip, target_port, count, port_range=(49152, 49190), delay=0.1, size_range=(50, 150)):
#     used_ports = set()  # Storing used port numbers to avoid duplicates within the session
#     for _ in range(count):
#         while True:
#             src_port = random.randint(*port_range)  # Randomly select a source port within the specified range
#             if src_port not in used_ports:
#                 used_ports.add(src_port)
#                 break
        
#         # Generate a random payload size within the specified range
#         payload_size = random.randint(*size_range)
#         payload = Raw(load='X'*payload_size)  # Create payload of 'X' characters


#         ipv6 = IPv6(dst=target_ip)
#         syn_packet = TCP(dport=target_port, flags="S")
    
#         # 发送 SYN 并接收 SYN/ACK
#         syn_ack_packet = sr1(ipv6/syn_packet, timeout=1)
#         # Construct the IPv6 and TCP layers
#         # ipv6 = IPv6(dst=target_ip)
#         ack_packet = TCP(dport=target_port, flags="A", seq=syn_ack_packet.ack, ack=syn_ack_packet.seq + 1)
#         # tcp = TCP(dport=target_port, sport=src_port,flags='A',)
#         send(ipv6/ack_packet)

#         psh_ack_packet = TCP(dport=target_port, flags="PA", seq=ack_packet.seq, ack=ack_packet.ack)
#         send(ipv6/psh_ack_packet/payload)
#         #packet = ipv6 / tcp / payload  # Assemble the packet layers with payload
        
#         # Send the packet and wait for the specified delay
#         #send(packet, verbose=False)


#         print(f"Sent {_+1}/{count} IPv6 TCP packet to {target_ip}:{target_port} from port {src_port} with size {payload_size} bytes")
#         #time.sleep(delay)  # Control the rate of packet sending

#         # Optionally clear the set of used ports if all possible ports have been used
#         if len(used_ports) >= (port_range[1] - port_range[0]):
#             used_ports.clear()

# if __name__ == "__main__":
#     target_ip = "2001:2:1::1"
#     target_port = 80
#     packet_count = 30
#     start_time = datetime.now()
#     flood_ipv6_tcp(target_ip, target_port, packet_count)
#     end_time = datetime.now()
    

#     formatted_time_start = start_time.strftime('%Y-%m-%d %H:%M:%S.%f')
#     formatted_time_end = end_time.strftime('%Y-%m-%d %H:%M:%S.%f')
#     print(f"normal completed.start_time {formatted_time_start}, end_time{formatted_time_end}")
import random
from datetime import datetime
from scapy.all import IPv6, TCP, send, Raw

def flood_ipv6_tcp(target_ip, target_port, count, port_range=(49152, 49190), delay=0.1):
    for _ in range(count):
        src_port = random.randint(*port_range)  # 随机选择一个源端口
        payload = Raw(load='Hello, this is a test!')  # 创建简单的负载
        
        # 构建IPv6和TCP层
        ipv6 = IPv6(dst=target_ip)
        tcp = TCP(dport=target_port, sport=src_port)
        packet = ipv6 / tcp / payload  # 组合数据包
        
        # 发送数据包
        send(packet, verbose=False)
        print(f"Sent {_+1}/{count} IPv6 TCP packet to {target_ip}:{target_port} from port {src_port}")
        #time.sleep(delay)  # 控制发送间隔

if __name__ == "__main__":
    target_ip = "2001:2:1::1"
    target_port = 80
    packet_count = 60

    # 记录开始时间
    start_time = datetime.now()

    # 发送数据包
    flood_ipv6_tcp(target_ip, target_port, packet_count)

    # 记录结束时间
    end_time = datetime.now()

    # 格式化时间
    formatted_time_start = start_time.strftime('%Y-%m-%d %H:%M:%S.%f')
    formatted_time_end = end_time.strftime('%Y-%m-%d %H:%M:%S.%f')
    print(f"Flooding completed. Start time: {formatted_time_start}, End time: {formatted_time_end}")


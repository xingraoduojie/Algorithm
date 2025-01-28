import os
import time

def main():
    # 定义要执行的命令
    commands = [
        "h1a python3 v6send.py &",
        "h1b python3 v6send.py &",
        "h1c python3 v6send.py &",
        "h3a python3 v6sendnormal.py &",
        "h3b python3 v6sendnormal.py &",
        "h3c python3 v6sendnormal.py"
    ]

    # 逐一执行每个命令
    for cmd in commands:
        os.system(cmd)
        time.sleep(1)  # 等待一秒以确保命令能够正确启动

    print("All commands executed.")

if __name__ == "__main__":
    main()

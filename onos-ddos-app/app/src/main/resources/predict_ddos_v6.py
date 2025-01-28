import sys
import joblib
import pandas as pd

# 加载模型和标准化器
model = joblib.load('knn_model.pkl')
scaler = joblib.load('scaler.pkl')

def predict_ddos(packet_features):
    df = pd.DataFrame([packet_features])
    X_new = df[['src_port', 'dst_port', 'protocol', 'length']]
    X_new = scaler.transform(X_new)
    y_new_pred = model.predict(X_new)
    return y_new_pred[0] == 0  # 0 表示 DDOS 攻击

if __name__ == "__main__":
    src_port = int(sys.argv[1])
    dst_port = int(sys.argv[2])
    protocol = int(sys.argv[3])
    length = int(sys.argv[4])
    
    packet_features = {
        'src_port': src_port,
        'dst_port': dst_port,
        'protocol': protocol,
        'length': length
    }
    
    is_ddos = predict_ddos(packet_features)
    print(is_ddos)

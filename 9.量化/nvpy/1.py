import requests
import websocket
import threading
import json
import ssl  # 加入这一行

# 代理配置
PROXY_HOST = "127.0.0.1"
PROXY_PORT = 7890
PROXIES = {
    "http": f"http://{PROXY_HOST}:{PROXY_PORT}",
    "https": f"http://{PROXY_HOST}:{PROXY_PORT}"
}

# 测试 Bybit 公共接口（HTTP）
def test_http():
    url = "https://api.bybit.com/v5/market/time"
    try:
        response = requests.get(url, proxies=PROXIES, timeout=10)
        print("✅ HTTP 连接成功，原始返回内容：", response.text)
        print("🔍 尝试解析 JSON：", response.json())
    except Exception as e:
        print("❌ HTTP 请求失败：", e)


# 测试 Bybit 公共 WebSocket（无需登录）
def test_websocket():
    ws_url = "wss://stream.bybit.com/v5/public/linear"
    try:
        def on_open(ws):
            print("✅ WebSocket 连接成功")
            ws.close()

        def on_error(ws, error):
            print("❌ WebSocket 连接错误：", error)

        def on_message(ws, message):
            # 这里只是演示如何接收消息，你可以根据需要做处理
            print(f"📡 收到消息：{message}")

        ws = websocket.WebSocketApp(
            ws_url,
            on_open=on_open,
            on_error=on_error,
            on_message=on_message
        )

        # 使用线程运行 WebSocket
        ws.run_forever(
            http_proxy_host=PROXY_HOST,  # 确保代理主机正确
            http_proxy_port=PROXY_PORT,  # 确保代理端口正确
            proxy_type="http",           # 明确指定代理类型为 http
            sslopt={"cert_reqs": ssl.CERT_NONE},  # 避免 SSL 验证
            on_close=lambda ws, close_status_code, close_msg: print("❌ WebSocket 已关闭")
        )

    except Exception as e:
        print("❌ WebSocket 连接失败：", e)


if __name__ == "__main__":
    print("🔍 正在测试 Bybit HTTP 接口...")
    test_http()

    print("\n🔍 正在测试 Bybit WebSocket 接口...")
    test_websocket()

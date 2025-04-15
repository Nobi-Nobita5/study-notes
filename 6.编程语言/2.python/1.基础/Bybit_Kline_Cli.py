#!/usr/bin/env python3
"""
命令行工具：从 Bybit HTTP API 拉取分钟级 K 线数据，并发下载，保存为 CSV。
使用方法示例：
python Bybit_Kline_Cli.py \
  --symbol BTCUSDT \
  --start "2025-04-14 11:00:00" \
  --end "2025-04-14 20:59:00" \
  --interval 1 \
  --category linear \
  --output btcusdt_20250414.csv

"""

import csv
import time
import math
from datetime import datetime

import requests
import click
from concurrent.futures import ThreadPoolExecutor, as_completed

# Bybit V5 API Get Kline endpoint
API_URL = "https://api.bybit.com/v5/market/kline"


def fetch_chunk(symbol, interval, category, start_ms, end_ms, limit):
    """
    拉取指定时间区间的 K 线数据（ms 精度），支持分页。
    参数：
        symbol: 交易对符号，如 BTCUSDT
        interval: K 线时间间隔（单位：分钟）
        category: 产品类别，如 linear、inverse、spot
        start_ms: 开始时间（毫秒）
        end_ms: 结束时间（毫秒）
        limit: 每页最大数量
    返回：
        所有拉取到的 K 线数据（二维数组）
    """
    results = []
    current_start = start_ms
    while current_start < end_ms:
        params = {
            "symbol": symbol,
            "interval": str(interval),
            "category": category,
            "start": current_start,
            "end": end_ms,
            "limit": limit
        }
        #params是关键字参数，必须params=params这样传递
        resp = requests.get(API_URL, params=params)
        resp.raise_for_status()
        data = resp.json()
        if data.get("retCode") != 0:
            raise RuntimeError(f"API error: {data}")
        candles = data["result"]["list"]
        if not candles:
            break
        # 返回结果按 startTime 倒序排列，取最后一条为最早的数据
        results.extend(candles)
        earliest = int(candles[-1][0])
        current_start = earliest + interval * 1000  # 避免重复
        time.sleep(0.1)  # 避免触发限流
    return results


@click.command()
@click.option("--symbol", required=True, help="交易对符号，如 BTCUSDT")
@click.option("--start", "start_time", required=True,
              type=click.DateTime(formats=["%Y-%m-%d %H:%M:%S"]),
              help="开始时间，格式：YYYY-MM-DD HH:MM:SS")
@click.option("--end", "end_time", required=True,
              type=click.DateTime(formats=["%Y-%m-%d %H:%M:%S"]),
              help="结束时间，格式：YYYY-MM-DD HH:MM:SS")
@click.option("--interval", default=1, show_default=True,
              type=int, help="K 线间隔（分钟），支持 1,3,5,...")
@click.option("--category", default="linear", show_default=True,
              type=click.Choice(["spot", "linear", "inverse"]), help="产品类型")
@click.option("--limit", default=200, show_default=True,
              type=int, help="单页最大记录数（1-1000）")
@click.option("--concurrency", default=5, show_default=True,
              type=int, help="并发线程数")
@click.option("--output", default="kline.csv", show_default=True,
              type=click.Path(), help="输出 CSV 文件路径")
def main(symbol, start_time, end_time, interval, category, limit, concurrency, output):
    """
    主函数：并发拉取数据并保存为 CSV
    步骤：
      1. 将开始/结束时间转换为毫秒时间戳
      2. 将整个时间区间分割成 concurrency 份
      3. 使用多线程并发下载每段时间区间的数据
      4. 合并、去重、排序，并写入 CSV 文件
    """
    start_ms = int(start_time.timestamp() * 1000)
    end_ms = int(end_time.timestamp() * 1000)
    total_span = end_ms - start_ms

    chunk_size = math.ceil(total_span / concurrency)
    ranges = []
    for i in range(concurrency):
        chunk_start = start_ms + i * chunk_size
        chunk_end = min(chunk_start + chunk_size - 1, end_ms)
        if chunk_start < chunk_end:
            ranges.append((chunk_start, chunk_end))

    all_data = []
    with ThreadPoolExecutor(max_workers=concurrency) as executor:
        # 字典推导式
        futures = {
            executor.submit(fetch_chunk, symbol, interval, category, rs, re, limit): (rs, re)
            for rs, re in ranges
        }
        for future in as_completed(futures):
            rs, re = futures[future]
            try:
                data = future.result()
                all_data.extend(data)
                click.echo(f"完成区间 {rs} - {re}，获取 {len(data)} 条数据")
            except Exception as e:
                click.echo(f"区间 {rs} - {re} 拉取失败：{e}", err=True)

    # 根据时间去重并升序排序，字典推导式
    unique = {int(item[0]): item for item in all_data}
    # 列表推导式
    sorted_items = [unique[k] for k in sorted(unique.keys())]

    # 写入 CSV
    with open(output, "w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["startTime", "open", "high", "low", "close", "volume", "turnover"])
        for row in sorted_items:
            writer.writerow(row)

    click.echo(f"已保存 {len(sorted_items)} 条数据到 {output}")


if __name__ == "__main__":
    main()

import pandas as pd  # 用于数据读取和处理
import numpy as np   # 用于数值计算
import click        # 用于构建命令行接口

@click.command()  # 声明下面的函数是一个 Click 命令行工具
@click.option(
    '--input', '-i',
    'input_file',
    default='btcusdt_20250414.csv',
    show_default=True,
    help='输入行情数据的 CSV 文件路径，必须包含 startTime, open, high, low, close, volume, turnover 列'
)
@click.option(
    '--output', '-o',
    'output_file',
    default='btcusdt_20250414_factors.csv',
    show_default=True,
    help='输出带因子数据的 CSV 文件路径'
)
@click.option(
    '--sma-window',
    default=20,
    show_default=True,
    help='SMA 和布林带中间线的滚动窗口大小（周期）'
)
@click.option(
    '--mom-window',
    default=1,
    show_default=True,
    help='动量因子计算的滞后期（以行数/条为单位）'
)
@click.option(
    '--bb-multiplier',
    default=2,
    show_default=True,
    help='布林带上下轨的标准差倍数'
)
def compute_factors(input_file, output_file, sma_window, mom_window, bb_multiplier):
    """
    读取本地 CSV，计算 SMA、动量、布林带因子，并输出新的 CSV

    参数：
      input_file: 输入行情 CSV 路径
      output_file: 输出因子 CSV 路径
      sma_window: 计算 SMA 和布林带中间线的周期
      mom_window: 计算动量的滞后期
      bb_multiplier: 布林带上下轨的标准差倍数
    """
    # 1. 读取 CSV 到 DataFrame，startTime 转换为 datetime 并设为索引
    df = pd.read_csv(input_file)
    df['startTime'] = pd.to_datetime(df['startTime'])
    df.set_index('startTime', inplace=True)

    # 2. 计算简单移动平均（SMA）
    df['SMA'] = df['close'].rolling(window=sma_window).mean()

    # 3. 计算动量（Momentum）
    df['Momentum'] = df['close'] - df['close'].shift(mom_window)

    # 4. 计算布林带（Bollinger Bands）
    df['BB_Middle'] = df['close'].rolling(window=sma_window).mean()
    df['BB_Std'] = df['close'].rolling(window=sma_window).std()
    df['BB_Upper'] = df['BB_Middle'] + bb_multiplier * df['BB_Std']
    df['BB_Lower'] = df['BB_Middle'] - bb_multiplier * df['BB_Std']

    # 5. 丢弃因子计算中产生的 NaN 行
    df.dropna(inplace=True)

    # 6. 保存到新的 CSV，包含原始数据和因子列
    df.to_csv(output_file)

    click.echo(f"已生成因子文件：{output_file}，共 {len(df)} 条记录。")

if __name__ == '__main__':
    compute_factors()  # 执行命令行工具

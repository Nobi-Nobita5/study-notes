#!/usr/bin/env python3
"""
Comprehensive Python syntax demo script.
Usage:
  python demo_syntax.py --help

python demo_syntax.py --mode stats --file numbers.txt

"""
# 模块导入：Python 内置库和类型提示工具
import argparse  # 命令行参数解析模块
import json       # JSON 读写模块
import logging    # 日志记录模块
from functools import wraps  # 用于装饰器的工具函数
from typing import List, Dict, Generator, Any  # 类型注解支持

# 配置日志格式和级别（推荐放在主入口之外）
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)  # 获取当前模块的 logger

# 装饰器示例：记录函数调用日志
def log_call(func):  # 定义装饰器函数
    @wraps(func)  # 保留原函数的元信息（如 __name__ 和 docstring）
    def wrapper(*args, **kwargs):  # 包裹函数的定义，接收任意参数
        logger.info(f"Calling {func.__name__} with args={args}, kwargs={kwargs}")
        result = func(*args, **kwargs)  # 调用原始函数
        logger.info(f"{func.__name__} returned {result}")
        return result  # 返回结果
    return wrapper

# 生成器示例：返回一个可迭代的斐波那契数列
def fibonacci(n: int) -> Generator[int, None, None]:
    """Generate first n Fibonacci numbers."""
    a, b = 0, 1
    for _ in range(n):
        yield a  # 使用 yield 声明生成器，每次返回一个值
        a, b = b, a + b

# 函数定义：打印欢迎语
def greet(name: str) -> None:  # 使用类型提示，返回 None
    """Print a greeting message."""
    print(f"Hello, {name}! Welcome to Python.")  # f-string 格式化字符串

# 函数定义：计算列表的基本统计值
def compute_stats(numbers: List[float]) -> Dict[str, Any]:
    """Compute basic statistics: count, sum, mean."""
    count = len(numbers)  # 元素个数
    total = sum(numbers)  # 求和
    mean = total / count if count else 0  # 均值，避免除以零
    return {'count': count, 'sum': total, 'mean': mean}  # 返回字典

@log_call  # 使用装饰器给函数添加日志功能
def stats_mode(numbers: List[float], output: str = None) -> None:
    stats = compute_stats(numbers)
    print("Statistics:")
    for key, value in stats.items():  # 遍历字典项
        print(f"  {key}: {value}")
    if output:
        # 使用上下文管理器打开文件（推荐做法）
        with open(output, 'w', encoding='utf-8') as f:
            json.dump(stats, f, indent=2)  # 写入 JSON 文件
        print(f"Saved stats to {output}")

# 类定义示例：一个简单的 Person 类
class Person:
    def __init__(self, name: str, age: int) -> None:  # 构造函数
        self.name = name  # 实例属性赋值
        self.age = age

    @property  # 只读属性装饰器
    def info(self) -> str:
        return f"Name: {self.name}, Age: {self.age}"

    def __repr__(self) -> str:  # 定义类的官方字符串表示
        return f"Person(name={self.name!r}, age={self.age})"

# 主函数入口：处理命令行参数和调度功能
def main() -> None:
    parser = argparse.ArgumentParser(description="Demo of Python syntax.")  # 创建解析器
    parser.add_argument('--mode', required=True, choices=['greet', 'stats', 'fib', 'person'],
                        help='Mode to run.')  # 必选参数
    parser.add_argument('--name', help='Name for greeting or person.')
    parser.add_argument('--age', type=int, help='Age for person.')  # 指定类型为整数
    parser.add_argument('--numbers', type=float, nargs='+', help='Numbers for stats.')  # 可接收多个浮点数
    parser.add_argument('--file', help='Filepath to read numbers (one per line).')
    parser.add_argument('--count', type=int, help='Count for Fibonacci sequence.')
    parser.add_argument('--output', help='Output JSON file for stats.')
    args = parser.parse_args()  # 解析参数

    if args.mode == 'greet':  # 分支逻辑控制
        if not args.name:
            parser.error('The --name argument is required for greet mode.')  # 手动抛出参数错误
        greet(args.name)

    elif args.mode == 'stats':
        nums = []
        if args.numbers:
            nums = args.numbers
        elif args.file:
            try:
                with open(args.file, 'r', encoding='utf-8') as f:
                    nums = [float(line.strip()) for line in f if line.strip()]  # 列表推导式读取数字
            except Exception as e:
                logger.error(f"Failed to read file: {e}")  # 错误处理
                return
        else:
            parser.error('Provide --numbers or --file for stats mode.')
        stats_mode(nums, args.output)

    elif args.mode == 'fib':
        if args.count is None:
            parser.error('The --count argument is required for fib mode.')
        print("Fibonacci sequence:")
        for num in fibonacci(args.count):  # 迭代生成器
            print(num)

    elif args.mode == 'person':
        if not args.name or args.age is None:
            parser.error('The --name and --age arguments are required for person mode.')
        p = Person(args.name, args.age)
        print(p.info)

# Python 的模块入口判断：仅在直接运行脚本时执行
if __name__ == '__main__':
    main()

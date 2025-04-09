# 自定义的数据服务模块，感觉可有可无

from .datafeed import BybitDatafeed


Datafeed = BybitDatafeed  # <- 这一行必须有

def get_datafeed_class():
    return Datafeed

def init():
    return BybitDatafeed()

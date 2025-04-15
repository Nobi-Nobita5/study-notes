def make_incrementor(n):
    return lambda x: x + n
print(make_incrementor(42))
#### 1.String被设计为不可变的好处？

String在Java中被设计为不可变的对象。这意味着一旦String对象被创建，它的值就不能被更改。任何修改String的操作都会创建一个新的String对象。

1. 不同的String变量之间不会相互影响，如str1 和 str2指向同一个String对象，该变str1，str2不会受影响
1. 可以安全的将String作为Map的键，由于String不可变，则每次hash计算的位置不会变
1. 也可以将其作为线程安全共享的对象。由于String不可变，对其多线程的操作一定是线程安全的


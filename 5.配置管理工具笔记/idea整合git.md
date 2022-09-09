1.VSC-> import into Version Control -> Create Git Repository。idea完成本地库的初始化操作
2.git - add，添加到暂存区，变绿色
3.git - commit 提交到本地库

4.git - push 到远程仓库

* 报错：**OpenSSL SSL_connect：443**，参考博客https://blog.csdn.net/qq_37555071/article/details/114260533

* 报错：OpenSSL SSL_read: SSL_ERROR_SYSCALL, errno 10054

​					是因为安全http.sslVerify安全设置没有修改，需要设置下参数

​				   git config http.sslVerify "false"

* 第一次登录时使用的token登录

​    
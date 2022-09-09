1. 登录网址[https://hub.docker.com](https://hub.docker.com/)搜索zookeeper，点击TAGS选择一个版本

2. 通过docker pull zookeeper:3.4.10就可以拉取指定版本的zookeeper镜像

3. 通过 docker images命令查看当前拉取的全部镜像

4. 启动zookeeper服务端

   docker run --privileged=true -d --name zookeeper --publish 2181:2181  -d zookeeper:3.4.10

   启动zookeeper客户端

   docker exec -it 7ec341fe8059 zkCli.sh


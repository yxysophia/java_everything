**Everything**

<font  size=4>**1.简介**</font>

仿照桌面工具Everything，基于Java语言开发的命令行搜索工具。

<font  size=4>**2.意义**</font>

 - 解决了Windows下的搜索盘符限制，可以在整个文件系统搜索；
 - 可以跨平台使用，比如在Windows、Linux系统；

<font  size=4>**3.功能**</font>
 - **文件名模糊检索：** 比如用户检索"报告"，可以检索出本系统中所有含有“报告”的文件；
 - **指定文件类型**：文件类型支持文档类（doc），二进制类(bin)，图片类（img）,其他（other），会检索出用户需要的文件类型
 - **用户友好**：用户可以指定检索的目录和排除的目录以及检索文件的数量和排序策略，否则就是默认值；
 - **文件监听**：当本地文件新增或删除，数据库会有相应的修改
 
<font  size=4>**4.技术**</font>
 
 - Java（基本语法、文件操作、多线程）
 - Database（嵌入式H2数据库）
 - JDBC编程
 - Lombok库（IDEA安装Lombok插件）
 - 文件系统监听（Apache Commons IO）

<font  size=4>**5.实现**</font>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20190227090646380.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3NvcGhpYV9feXU=,size_16,color_FFFFFF,t_70)
 
 **5.1     H2数据库**

 - 开源的Java语言编写的嵌入式数据库
 - 内存小
 - 以本地文件的方式存储在磁盘上，只需要提供URL接口，不需要账号和密码
 - H2数据库可以随着程序一起发布，而MySQL是分布式发布

  **5.2  索引**

 - 指定目录建立索引
 - 指定排除目录不建立索引（如windows系统下的目录）

  **5.3  检索** 

 - 根据用户指定信息进行模糊检索
 - 在检索过程可以过滤数据库找中本地不存在的文件
 
  **5.4 文件监听** 

 - Java程序运行在JVM虚拟机，无法对操作系统文件变化做出直接响应，有2种方式可以监听文件
 - 方法1：利用FileSystem提供文件系统的接口，WatchService接口监听文件系统变化，但是只能监听一级目录
 - 方法2：引入Apache Commons  IO开源库，不停的遍历文件。由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，如果有文件的变化，则根据相关的文件比较器FileAlterationListener的实现类来判断文件时新增，还是删除，还是更改。
 FileAlterationListener专门用来接收文件系统的通知

 **5.4 命令行交互** 

 - 程序入口解析和配置：配置检索目录、排除目录、检索文件最大数量、排序策略

 - 交互式执行：帮助（help），索引（index），检索（search）,退出（exit）

<font  size=4>**6.用法**</font>

 **6.1使用**
```
java -jar  java_everything-1.0.0.jar args
```
args：

 - --maxReturn=20 ：返回检索文件最大数量，输入将默认为30
 - --depthAsc=true/false：检索文件排序策略，默认为按照文件深度升序
 - --includePath=D:\ ，默认为C:\, E:\, D:\, F:\
 - --excludePath=E:\a,C:\Windows，默认为C:\Windows, C:\Program Files, C:\Users, C:\Program Files (x86）
 
 **6.2 命令**
 
命令列表:
帮助：help
退出：exit
索引:index
检索：search fileName [fileType: img doc bin other]

 **6.3检索**
 
不指定文件类型：
```
please input your require:
search java
D:\java_ideal
C:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$II5NNUS.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RP8CE3K.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RMCQBYP.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RVOO91Q.java
D:\VScode\work\Work2.java
D:\VScode\work\R.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RNE5IIV.java
D:\VScode\work\Sort.java
D:\VScode\work\Role.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RHY3Y3P.java
D:\apache-maven-3.5.0\lib\javax.inject-1.jar
C:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RI5NNUS.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RBCNPQK.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RC0LCVR.java
C:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$IG2TV6R.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RDEUPYT.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RF1DSGG.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RT6JUJ6.java
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RUDQ80N.java
```
指定文件类型

```
please input your require:
search java doc
D:\java_ideal\IntelliJ IDEA 2018.1.6\license\javaeeJar-CDDLv1.0.txt
C:\Users\lenovo\Desktop\java.txt
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RMUW1XY.6\license\javaeeJar-CDDLv1.0.txt
C:\Users\lenovo\eclipse-workspace\.metadata\.plugins\org.eclipse.jdt.core\javaLikeNames.txt
C:\Users\lenovo\Documents\Tencent Files\2049161978\FileRecv\MobileFile\java3.txt
D:\java_ideal\IntelliJ IDEA 2018.1.6\plugins\Kotlin\kotlinc\license\third_party\testdata\rxjava_license.txt
D:\$RECYCLE.BIN\S-1-5-21-3937788977-1081121814-3639257697-1002\$RMUW1XY.6\plugins\Kotlin\kotlinc\license\third_part
```

<font  size=4>**7.收获**</font>

 - 学会用H2数据库来创建数据库，用数据源建立与数据库的连接
 - 锻炼了分析bug的能力以及编码能力
 - 熟悉Java的基本语法在实际项目中的应用，比如接口编程、线程池使用、单例模式、反射等

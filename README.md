# port-mux
这是一个端口复用工具，可以在一个端口上提供SSH、HTTP、VNC等多种服务。

此项目参考了[switcher](https://github.com/jackyspy/switcher) 项目的思想，用 Kotlin 和 Netty 来实现。

此工具还支持监听配置文件的更新从而动态更新程序配置。

## 用法
- 运行环境: jdk8
- 启动命令:
```
java -Xmx10m -jar port-mux.jar
```
- 帮助命令
```
java -jar port-mux.jar --help
Usage: java -jar port-mux.jar [options]
  Options:
    --help

    -config
      配置文件路径
      Default: config.json5
    -watch-config
      是否监听配置文件改变
      Default: true
```

### config.json5
程序配置文件
```json5
{
  // 监听地址，不支持动态更新
  listen: ":80",
  // 转发服务使用的线程数，默认为CPU核心数的2倍，不支持动态更新
  thread_num: 4,
  // 日志级别
  log_level: "info",
  // 打印转发数据方式，可选值: string, byte, hex, pretty_hex
  log_data_type: "",
  // 打印转发数据的长度，默认为全部
  log_data_len: 10,
  // 默认转发地址
  default: "127.0.0.1:8080",
  // 连接转发地址超时时间(毫秒)
  connect_timeout: 1000,
  // 读取超时时间(毫秒)
  read_timeout : 1000,
  // 读取超时的转发地址
  read_timeout_address: "127.0.0.1:5900",
  // 匹配超时时间(毫秒)
  match_timeout: 5000,
  // 转发协议配置
  protocols: [
    {
      name: "ssh",
      type: "prefix",
      addr: "127.0.0.1:22",
      patterns: ["SSH"]
    },
    {
      name: "http",
      type: "prefix",
      addr: "127.0.0.1:8080",
      patterns: ["GET ", "POST ", "PUT ", "DELETE ", "HEAD ", "OPTIONS "]
    },
    {
      name: "Windows远程连接",
      type: "hex",
      addr: "127.0.0.1:3389",
      patterns: ["030000130ee00000000000010008000b000000"]
    }
  ]
}
```

## 匹配类型
### prefix
```json5
{
  // 协议名称
  name: "ssh",
  // 匹配类型
  type: "prefix",
  // 转发地址
  addr: "127.0.0.1:22",
  // 匹配前缀字符串
  patterns: ["SSH"]
}
```

### regex
注意：regex 类型不支持多次匹配，只会匹配第一次接收到的数据
```json5
{
  // 协议名称
  name: "http_regex",
  // 匹配类型
  type: "regex",
  // 转发地址
  addr: "127.0.0.1:80",
  // 最小匹配字节数，这里的意思是小于4字节的数据直接算匹配失败
  min_len: 4,
  // 最大匹配字节数，这里的意思是只会把前8个字节的数据转为字符串进行匹配
  max_len: 8,
  patterns: ["^(GET|POST|PUT|DELETE|HEAD|OPTIONS) "]
}
```

### bytes
```json5
{
  // 协议名称
  name: "Windows远程连接",
  // 匹配类型
  type: "bytes",
  // 转发地址
  addr: "127.0.0.1:3389",
  // 匹配前缀字节数组
  patterns: ["3, 0, 0, 19, 14, -32, 0, 0, 0, 0, 0, 1, 0, 8, 0, 11, 0, 0, 0"]
}
```

### hex
```json5
{
  // 协议名称
  name: "Windows远程连接",
  // 匹配类型
  type: "hex",
  // 转发地址
  addr: "127.0.0.1:3389",
  // 匹配前缀十六进制字节数组
  patterns: ["030000130ee00000000000010008000b000000"]
}
```

## 调试
```json5
{
  // 日志级别
  log_level: "debug",
  // 打印转发数据格式，可选值: string, byte, hex, pretty_hex
  log_data_type: "pretty_hex",
}
```
1. `log_level` 设置为 `debug` 级别
2. 设置 `log_data_type` 为你想要的打印格式

### log_data_type
#### string
```text
2021-12-30 10:10:14.006 DEBUG [nioEventLoopGroup-3-1] org.ffpy.portmux.server.MatchHandler     : /127.0.0.1:51012 发送数据(78): GET / HTTP/1.1
Host: 127.0.0.1:8200
User-Agent: curl/7.68.0
Accept: */*


```

#### byte
```text
2021-12-30 10:11:57.554 DEBUG [nioEventLoopGroup-3-3] org.ffpy.portmux.server.MatchHandler     : /127.0.0.1:51668 发送数据(78): [71, 69, 84, 32, 47, 32, 72, 84, 84, 80, 47, 49, 46, 49, 13, 10, 72, 111, 115, 116, 58, 32, 49, 50, 55, 46, 48, 46, 48, 46, 49, 58, 56, 50, 48, 48, 13, 10, 85, 115, 101, 114, 45, 65, 103, 101, 110, 116, 58, 32, 99, 117, 114, 108, 47, 55, 46, 54, 56, 46, 48, 13, 10, 65, 99, 99, 101, 112, 116, 58, 32, 42, 47, 42, 13, 10, 13, 10]
```

#### hex
```text
2021-12-30 10:12:27.446 DEBUG [nioEventLoopGroup-3-4] org.ffpy.portmux.server.MatchHandler     : /127.0.0.1:51687 发送数据(78): 474554202f20485454502f312e310d0a486f73743a203132372e302e302e313a383230300d0a557365722d4167656e743a206375726c2f372e36382e300d0a4163636570743a202a2f2a0d0a0d0a
```

#### pretty_hex
```text
2021-12-30 10:12:48.586 DEBUG [nioEventLoopGroup-3-5] org.ffpy.portmux.server.MatchHandler     : /127.0.0.1:51702 发送数据(78): 
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 47 45 54 20 2f 20 48 54 54 50 2f 31 2e 31 0d 0a |GET / HTTP/1.1..|
|00000010| 48 6f 73 74 3a 20 31 32 37 2e 30 2e 30 2e 31 3a |Host: 127.0.0.1:|
|00000020| 38 32 30 30 0d 0a 55 73 65 72 2d 41 67 65 6e 74 |8200..User-Agent|
|00000030| 3a 20 63 75 72 6c 2f 37 2e 36 38 2e 30 0d 0a 41 |: curl/7.68.0..A|
|00000040| 63 63 65 70 74 3a 20 2a 2f 2a 0d 0a 0d 0a       |ccept: */*....  |
+--------+-------------------------------------------------+----------------+
```

## License
port-mux is licensed under the Apache License, Version 2.0 
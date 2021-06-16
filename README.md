# socket-forward

使用此工具可以实现通过一个端口，来代理多个其他端口的连接。  
例如可以用于内网穿透，用一个端口来提供多个服务，如HTTP、SSH、Windows远程连接等。
<br><br>
此项目参考了[switcher](https://github.com/jackyspy/switcher) 项目的思想，用kotlin和Netty来实现，
还额外添加了字节数组前缀匹配的功能。

## 用法
- 运行环境: jdk11
- 启动命令:
```
java -jar socket-forward.jar
```
- 帮助命令
```
java -jar socket-forward.jar --help
Usage: <main class> [options]
  Options:
    --help

    -config
      配置文件路径
      Default: config.json5
    -debug
      启用调试模式，打印转发数据，可选值: string, byte
      Default: <empty string>
```

### config.json5
在此文件中配置代理规则
```json5
{
  // 监听地址
  listen: ":80",
  // 默认转发地址
  default: "127.0.0.1:8080",
  // 连接转发地址超时时间(毫秒)
  connect_timeout: 1000,
  // 读取超时时间(毫秒)
  read_timeout : 1000,
  // 读取超时的转发地址
//  read_timeout_address: "127.0.0.1:5900",
  // 转发协议配置
  protocols: [
    //    // 示例
    //    {
    //      // 名称
    //      name: "http",
    //      // 类型，可选值: prefix(前缀匹配), regex(正则匹配), bytes(字节前缀匹配)
    //      type: "regex",
    //      // 转发地址
    //      addr: "127.0.0.1:8080",
    //      // 匹配最短字节数，regex类型才有效
    //      min_len: 4,
    //      // 匹配最长字节数，regex类型才有效
    //      max_len: 7,
    //      // 匹配字符串
    //      patterns: ["^(GET|POST|PUT|DELETE|HEAD|OPTIONS) "]
    //    },
    {
      name: "ssh",
      type: "prefix",
      addr: "127.0.0.1:5000",
      patterns: ["SSH"]
    },
    {
      name: "http",
      type: "prefix",
      addr: "127.0.0.1:8080",
      patterns: ["GET ", "POST ", "PUT ", "DELETE ", "HEAD ", "OPTIONS "]
    },
//    {
//      name: "Windows远程连接",
//      type: "bytes",
//      addr: "127.0.0.1:3389",
//      patterns: ["3, 0, 0, 19, 14, -32, 0, 0, 0, 0, 0, 1, 0, 8, 0, 11, 0, 0, 0"]
//    }
  ]
}
```

## License
socket-forward is licensed under the Apache License, Version 2.0 
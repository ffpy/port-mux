# port-mux
这是一个端口复用工具，可以在一个端口上提供SSH、HTTP、VNC等多种服务。

此项目参考了[switcher](https://github.com/jackyspy/switcher) 项目的思想，用 kotlin 和 Netty 来实现，还添加了字节数组前缀匹配的功能。

此工具还支持监听配置文件的更新从而不停机更新程序配置。

## 用法
- 运行环境: jdk11
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
  // 监听地址，不支持不停机更新
  listen: ":8200",
  // 启用调试模式，打印转发数据，可选值: string, byte
  debug: "",
  // 默认转发地址
  default: "127.0.0.1:8080",
  // 连接转发地址超时时间(毫秒)，不支持不停机更新
  connect_timeout: 1000,
  // 读取超时时间(毫秒)
  read_timeout : 1000,
  // 读取超时的转发地址
  read_timeout_address: "127.0.0.1:5900",
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
      addr: "127.0.0.1:22",
      patterns: ["SSH"]
    },
    {
      name: "http",
      type: "prefix",
      addr: "127.0.0.1:80",
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
port-mux is licensed under the Apache License, Version 2.0 
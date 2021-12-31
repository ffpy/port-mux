# port-mux
[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](README.md)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](README_zh_CN.md)

This is a proxy server that allows you to connect SSH, HTTP, VNC and other services on same port.

This project refers to the idea of [switcher](https://github.com/jackyspy/switcher), 
and is implemented by Kotlin and Netty.

By configuring in the configuration file, the traffic on the listening port can be easily forwarded to
SSH, HTTP, VNC and other ports. This tool can be used for NAT traversal, limited number of outer net, etc.

This tool also supports listening to the modification of the configuration file, 
so the program configuration can be updated without restarting the program.

## Usage
- Requires: JDK8 or later
- Start command:
```bash
java -Xmx20m -jar port-mux.jar
```
- Help
```
Usage: java -jar port-mux.jar [options]
  Options:
    --help

    -config
      The path of configuration file.
      Default: config.json5
    -epoll
      Whether to use epoll mode. Epoll mode has better performance, but some systems do not support this mode.
      Default: false
    -watch-config
      Listening for modification of configuration file.
      Default: true
```

### config.json5
The configuration file for this tool.

The following is an example, listening on port 8200
- SSH traffic is forwarded to port 22
- HTTP traffic is forwarded to port 8080
- RDP traffic is forwarded to port 3389
- Other traffic is forwarded to port 8080
```json5
{
  listen: ":8200",
  default: "127.0.0.1:8080",
  read_timeout_address: "127.0.0.1:5900",
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
      name: "RDP",
      type: "hex",
      addr: "127.0.0.1:3389",
      patterns: ["030000130ee00000000000010008000b000000"]
    }
  ]
}
```

#### All items of configuration
```json5
{
  // Listening address. Dynamic update is not supported for this
  listen: ":80",
  // The number of threads used by the forwarding service. The default value is twice the number of cpu cores. Dynamic update is not supported for this
  thread_num: 4,
  // The level for logging. The default value is info
  log_level: "info",
  // The format for printing data. The optional values are string, byte, hex, pretty_hex
  log_data_type: "pretty_hex",
  // The length of printed data. The default value is 1000
  log_data_len: 1000,
  // Default forwarding address
  default: "127.0.0.1:8080",
  // Timeout for connecting to forwarding address. The default value is 1000
  connect_timeout: 1000,
  // Timeout for reading data. The default value is 1000
  read_timeout : 1000,
  // The forwarding address if read timeout
  read_timeout_address: "127.0.0.1:5900",
  // Timeout for protocol matching. The default value is 5000
  match_timeout: 5000,
  // The list of forwarding protocols
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
      name: "RDP",
      type: "hex",
      addr: "127.0.0.1:3389",
      patterns: ["030000130ee00000000000010008000b000000"]
    }
  ]
}
```

## Protocol type
### prefix
```json5
{
  // The name of protocol
  name: "ssh",
  // The type of protocol
  type: "prefix",
  // Forwarding address
  addr: "127.0.0.1:22",
  // The prefix string for matching
  patterns: ["SSH"]
}
```

### regex
Note：The regex type does not support multiple matches. It only matches the data received for the first time.
```json5
{
  // The name of protocol
  name: "http_regex",
  // The type of protocol
  type: "regex",
  // Forwarding address
  addr: "127.0.0.1:80",
  // The minimum number of matching bytes, which means that data less than 4 bytes will fail to match
  min_len: 4,
  // The maximum number of matching bytes, which means that only the first 8 bytes of data will be converted to a string for matching
  max_len: 8,
  // The regular expression for matching
  patterns: ["^(GET|POST|PUT|DELETE|HEAD|OPTIONS) "]
}
```

### bytes
```json5
{
  // The name of protocol
  name: "RDP",
  // The type of protocol
  type: "bytes",
  // Forwarding address
  addr: "127.0.0.1:3389",
  // The byte array value for matching
  patterns: ["3, 0, 0, 19, 14, -32, 0, 0, 0, 0, 0, 1, 0, 8, 0, 11, 0, 0, 0"]
}
```

### hex
```json5
{
  // The name of protocol
  name: "RDP",
  // The type of protocol
  type: "hex",
  // Forwarding address
  addr: "127.0.0.1:3389",
  // The hex bytes for matching
  patterns: ["030000130ee00000000000010008000b000000"]
}
```

## Debug
You can print the data content in the forwarding process.
```json5
{
  // The level for logging
  log_level: "debug",
  // The format for printing data. The optional values are string, byte, hex, pretty_hex
  log_data_type: "pretty_hex",
  // The length of printed data. The default value is 1000
  log_data_len: 1000,
}
```
1. Set `log_level` to `debug` level.
2. Set `log_data_type` to the print format you want.

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
# 并发测试工具
常见的测试工具有Postman, AB, jMeter
## Apache bench
`ab -n 1000 -c 50 http://localhost:8040/hello`
- 请求参数
    * -n : 请求次数
    * -c : 并发线程数
- 请求结果
    ```bash
    Document Path:          /hello
    Document Length:        11 bytes
    
    Concurrency Level:      50 # 并发量
    Time taken for tests:   0.186 seconds # 整个测试所用时间
    Complete requests:      1000 # 完成的请求数
    Failed requests:        0
    Total transferred:      144000 bytes # 所有请求的响应数据的长度总和(header + 正文)
    HTML transferred:       11000 bytes # 所有请求的响应数据的正文数据长度总和
    # 吞吐率，它与并发数相关，即使请求总数相同，但如果并发数不一样，吞吐率也有很大差异
    # Complete requests / Time taken for tests
    Requests per second:    5365.35 [#/sec] (mean)
    Time per request:       9.319 [ms] (mean) # 用户平均请求等待时间
    Time per request:       0.186 [ms] (mean, across all concurrent requests) # 服务器平均请求等待时间
    # 请求单位时间内从服务器获取的数据长度，Total transferred / Time taken for tests
    Transfer rate:          754.50 [Kbytes/sec] received
    
    Connection Times (ms)
                  min  mean[+/-sd] median   max
    Connect:        0    3   3.3      2      24
    Processing:     1    6   4.9      4      29
    Waiting:        1    5   4.0      4      25
    Total:          3    9   6.4      6      36
    
    Percentage of the requests served within a certain time (ms)
      50%      6
      66%      7
      75%      8
      80%     12
      90%     19
      95%     23
      98%     31
      99%     32
     100%     36 (longest request)
    ```
# 农残检测上传协议

本文档描述 `PesticideTestActivity2` 点击“上传数据”时当前版本实际使用的上传格式。协议以代码为准，服务端不能按标准 JSON 解析请求中的 `result` 对象，因为客户端当前生成的是单引号对象文本。

## 调用流程

1. `PesticideTestActivity2.upload(true)` 检查网络和已选检测结果。
2. 创建 `UploadThread`，每个有效的 `CheckResult` 单独调用一次 `ToolUtils.assemblyUploadData`。
3. `UploadThread` 使用 OkHttp 向 `Global.uploadUrl` 发起 POST。
4. 响应正文包含 `上传成功` 时调用 `onSuccess`；否则调用 `onFail`。
5. `PesticideTestActivity2` 的成功回调调用 `updateUploadState2Db`，按现有逻辑更新本地记录。

## 地址与认证配置

上传地址和账号来自系统设置，应用首次启动的默认值为：

| 配置 | 默认值 |
| --- | --- |
| URL | `http://112.5.17.83:8081/get.aspx` |
| `username` | `0109001` |
| `password` | `0109001` |

模拟器联调时，把上传地址设置为 `http://10.0.2.2:8080/get.aspx`。`10.0.2.2` 是 Android Emulator 访问宿主机的特殊地址。

## HTTP 请求

- 方法：`POST`
- 路径：通常为 `/get.aspx`
- Content-Type：`application/x-www-form-urlencoded; charset=utf-8`
- 实际字节编码：客户端将请求体按 GB2312 写入
- 表单字段：`result`
- 每条检测记录一个请求；多选记录会产生多个独立 POST

请求体的形态如下（示例中的中文按 GB2312 编码）：

```text
result={cmd:'3',username:'0109001',password:'0109001',items:[{spdm:'123',spmc:'苹果',jcxm:'有机磷和氨基甲酸酯类农药',xmmc:'13',jcz:'12',jcjg:'合格',jcrq:'2026-10-12 20:00:00',twh:'A1',weight:'1',cljg:'合格'}]}
```

注意：这是当前客户端拼接的伪 JSON 文本，键和值使用单引号，不能直接当作严格 JSON。

## `result` 字段

外层对象字段：

| 字段 | 来源或含义 |
| --- | --- |
| `cmd` | 固定为字符串 `3` |
| `username` | `Global.TESTING_UNIT_NUMBER` |
| `password` | `Global.TESTING_UNIT_NAME` |
| `items` | 包含一个检测明细对象的数组文本 |

明细字段：

| 字段 | 来源或处理 |
| --- | --- |
| `spdm` | `DbHelper.getSampleNumber(result.sampleName)`，去除空格 |
| `spmc` | `CheckResult.sampleName` |
| `jcxm` | `CheckResult.projectName` |
| `xmmc` | 当前代码固定为 `13` |
| `jcz` | `CheckResult.testValue`；非数字时为 `0` |
| `jcjg` | 归一化后的判定结果 |
| `jcrq` | `testTime`，格式 `yyyy-MM-dd HH:mm:ss` |
| `twh` | `CheckResult.twh` |
| `weight` | `CheckResult.weight`；空值时为 `1` |
| `cljg` | 与 `jcjg` 相同 |

判定结果的现有归一化规则：`阴性` 转为 `合格`，`阳性` 转为 `不合格`；其他不是 `合格` 或 `不合格` 的值默认转为 `合格`。

## HTTP 响应

模拟服务器和真实服务端至少应返回：

```http
HTTP/1.1 200 OK
Content-Type: application/json; charset=utf-8

{"ret":"上传成功"}
```

响应正文使用 UTF-8 编码，成功信息放在 `ret` 字段中。客户端仍只检查响应正文是否包含“上传成功”，不解析 JSON 或依赖 HTTP 状态码进行成功判断。正文不包含该文字时会进入失败回调。错误响应也使用相同格式，例如 `{"ret":"缺少 result 参数"}`。

## 启动模拟服务器

在项目根目录运行：

```powershell
python tools/mock-server/mock_upload_server.py
```

默认监听 `0.0.0.0:8080`，接口为 `http://localhost:8080/get.aspx`。也可以指定端口：

```powershell
python tools/mock-server/mock_upload_server.py --host 0.0.0.0 --port 8080
```

服务器会打印原始请求体、账号、命令和完整 `result`。缺少 `result`、路径不是 `/get.aspx` 或必要字段不完整时返回 HTTP `400`。

## 命令行验证

PowerShell 中可用 Python 发送一条 GB2312 请求：

```powershell
@'
import urllib.request

data = "result={cmd:'3',username:'0109001',password:'0109001',items:[{spdm:'123',spmc:'苹果',jcxm:'有机磷',xmmc:'13',jcz:'12',jcjg:'合格',jcrq:'2026-10-12 20:00:00',twh:'A1',weight:'1',cljg:'合格'}]}".encode('gb2312')
request = urllib.request.Request(
    'http://127.0.0.1:8080/get.aspx',
    data=data,
    headers={'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8'},
    method='POST')
print(urllib.request.urlopen(request).read().decode('gb2312'))
'@ | python
```

预期输出为 `{"ret":"上传成功"}`。

## Android Emulator 联调

1. 在电脑上启动模拟服务器。
2. 在应用系统设置的上传地址中填写 `http://10.0.2.2:8080/get.aspx`。
3. 保存设置并完成一条检测记录。
4. 点击“上传数据”，观察应用提示“上传成功”和服务器控制台日志。
5. 若连接失败，检查 Windows 防火墙是否允许 Python 监听 TCP `8080`；模拟器不能使用宿主机的 `localhost`。

真机联调时，服务器绑定 `0.0.0.0`，应用地址应改为电脑在同一局域网中的 IPv4 地址，例如 `http://192.168.1.20:8080/get.aspx`。

## 当前客户端行为限制

- 每个选中的结果独立上传，服务端不能假设一次请求包含全部选中记录。
- 成功判定只依赖响应正文包含“上传成功”。
- 本次联调不修改客户端现有上传状态和回调实现。

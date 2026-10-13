#!/usr/bin/env python3
"""Local upload endpoint compatible with the Android application's protocol."""

import argparse
import copy
import json
import re
import threading
from datetime import datetime
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from urllib.parse import parse_qs


FIELD_PATTERN = re.compile(r"([A-Za-z][A-Za-z0-9]*):'([^']*)'")
REQUIRED_FIELDS = {"cmd", "username", "password"}
ITEM_FIELDS = ("spdm", "spmc", "jcxm", "xmmc", "jcz", "jcjg", "jcrq", "twh", "bjdw", "ybcd", "weight", "cljg")
UPLOADS = []
UPLOADS_LOCK = threading.Lock()


def decode_body(raw_body, content_type):
    """Decode the request using its declared charset, defaulting to GB2312."""
    charset = "gb2312"
    match = re.search(r"charset=([^;\s]+)", content_type or "", re.IGNORECASE)
    if match:
        charset = match.group(1).strip('"')
    try:
        return raw_body.decode(charset)
    except (LookupError, UnicodeDecodeError):
        return raw_body.decode("gb2312", errors="replace")


def parse_result(result):
    """Parse the application's pseudo-JSON object into a log-friendly mapping."""
    if not result or not result.startswith("{") or not result.endswith("}"):
        raise ValueError("result must be a non-empty object")
    fields = dict(FIELD_PATTERN.findall(result))
    missing = REQUIRED_FIELDS - fields.keys()
    if missing:
        raise ValueError("missing fields: " + ", ".join(sorted(missing)))
    item_match = re.search(r"items:\[\{(.*?)\}\]", result)
    if not item_match:
        raise ValueError("items must contain one item object")
    item_values = dict(FIELD_PATTERN.findall(item_match.group(1)))
    item = {name: item_values.get(name, "") for name in ITEM_FIELDS}
    return {"cmd": fields["cmd"], "username": fields["username"],
            "password": fields["password"], "items": [item]}


def snapshot_uploads():
    with UPLOADS_LOCK:
        return copy.deepcopy(UPLOADS)


PAGE = """<!doctype html><html lang="zh-CN"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>农残上传模拟服务器</title><style>
body{font:14px system-ui,"Microsoft YaHei",sans-serif;margin:0;background:#f4f6f8;color:#20252b}main{max-width:1500px;margin:0 auto;padding:24px}h1{font-size:24px;margin:0 0 8px}.meta{color:#5e6872;margin-bottom:18px}.table-wrap{overflow:auto;background:#fff;border:1px solid #dfe4e8;border-radius:6px}table{border-collapse:collapse;width:100%;min-width:1180px}th,td{border:1px solid #e1e5e8;padding:8px;text-align:left;vertical-align:top}thead tr:first-child th{background:#e7edf2;font-weight:normal;color:#52606b}thead tr:nth-child(2) th{background:#f0f3f5;white-space:nowrap}.empty{padding:28px;text-align:center;color:#74808b;background:#fff;border:1px solid #dfe4e8;border-radius:6px}.action{color:#176b35;background:#e7f5ec;border:1px solid #b8dfc4;border-radius:4px;padding:5px 9px;cursor:pointer;white-space:nowrap}.modal{display:none;position:fixed;inset:0;background:#0008;align-items:center;justify-content:center;padding:18px}.modal.open{display:flex}.modal-box{background:#fff;width:min(900px,100%);max-height:90vh;overflow:auto;border-radius:6px;padding:20px;box-sizing:border-box}.modal-head{display:flex;justify-content:space-between;align-items:center}.modal-head h2{margin:0;font-size:18px}.close{border:0;background:transparent;font-size:24px;cursor:pointer;color:#5e6872}pre{white-space:pre-wrap;word-break:break-all;background:#f7f8f9;padding:12px;border-radius:4px}.type{color:#687782;font-size:12px}
</style></head><body><main><h1>农残上传数据</h1><div class="meta">进程内累计上传：<strong id="count">0</strong> 条；重启服务器后清空</div><section id="records"><div class="empty">等待上传数据...</div></section></main>
<div id="rawModal" class="modal" onclick="if(event.target===this)closeRaw()"><div class="modal-box"><div class="modal-head"><h2>原始上传数据</h2><button class="close" onclick="closeRaw()">&times;</button></div><div id="rawDetails"></div></div></div>
<script>
const esc=s=>String(s??'').replace(/[&<>"']/g,c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));const fields=['spdm','spmc','jcxm','xmmc','jcz','jcjg','jcrq','twh','bjdw','ybcd','weight','cljg'];const labels=['样品编码','样品名称','检测项目','项目代码','检测值','检测判定','检测时间','通道号/托盘位','被检单位','商品来源','样品重量','处理结果'];const types=['string','string','string','string','numeric string','string','datetime string','string','string','string','string','string'];let uploads=[];
function render(data){uploads=data.uploads;document.getElementById('count').textContent=data.count;const box=document.getElementById('records');if(!uploads.length){box.innerHTML='<div class="empty">等待上传数据...</div>';return;}const head1=labels.map(x=>`<th>${x}</th>`).join('')+'<th>原始数据</th>';const head2=fields.map(f=>`<th>${f}</th>`).join('')+'<th>查看</th>';const rows=[];uploads.forEach((u,i)=>u.items.forEach(item=>rows.push(`<tr>${fields.map(f=>`<td>${esc(item[f])}</td>`).join('')}<td><button class="action" onclick="showRaw(${i})">查看原始数据</button></td></tr>`)));box.innerHTML=`<div class="table-wrap"><table><thead><tr>${head1}</tr><tr>${head2}</tr></thead><tbody>${rows.join('')}</tbody></table></div>`;}
function showRaw(index){const u=uploads[index];const item=u.items[0]||{};const details=`<p>接收时间：${esc(u.received_at)}<br>请求路径：${esc(u.path)}</p><table><thead><tr><th>字段</th><th>类型</th><th>值</th></tr></thead><tbody><tr><td>cmd</td><td>string</td><td>${esc(u.cmd)}</td></tr><tr><td>username</td><td>string</td><td>${esc(u.username)}</td></tr><tr><td>password</td><td>string</td><td>${esc(u.password)}</td></tr>${fields.map((f,i)=>`<tr><td>${f}</td><td>${types[i]}</td><td>${esc(item[f])}</td></tr>`).join('')}</tbody></table><h3>result</h3><pre>${esc(u.result)}</pre><h3>原始请求体</h3><pre>${esc(u.raw_body)}</pre>`;document.getElementById('rawDetails').innerHTML=details;document.getElementById('rawModal').classList.add('open');}function closeRaw(){document.getElementById('rawModal').classList.remove('open');}async function refresh(){try{const r=await fetch('/api/uploads',{cache:'no-store'});if(r.ok)render(await r.json());}catch(e){}}refresh();setInterval(refresh,1000);
</script></body></html>"""


class UploadHandler(BaseHTTPRequestHandler):
    server_version = "PesticideMockUpload/1.0"

    def log_message(self, fmt, *args):
        print("[%s] %s" % (self.log_date_time_string(), fmt % args), flush=True)

    def send_json(self, status, ret):
        body = json.dumps({"ret": ret}, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def send_bytes(self, status, body, content_type):
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

    def do_GET(self):
        path = self.path.split("?", 1)[0]
        if path == "/":
            self.send_bytes(200, PAGE.encode("utf-8"), "text/html; charset=utf-8")
        elif path == "/api/uploads":
            records = snapshot_uploads()
            payload = json.dumps({"count": len(records), "uploads": list(reversed(records))}, ensure_ascii=False).encode("utf-8")
            self.send_bytes(200, payload, "application/json; charset=utf-8")
        elif path == "/get.aspx":
            self.send_json(405, "只支持 POST")
        else:
            self.send_json(404, "接口不存在")

    def do_POST(self):
        if self.path.split("?", 1)[0] != "/get.aspx":
            self.send_json(404, "接口不存在")
            return

        try:
            length = int(self.headers.get("Content-Length", "0"))
        except ValueError:
            self.send_json(400, "Content-Length 无效")
            return

        body = decode_body(self.rfile.read(length), self.headers.get("Content-Type", ""))
        form = parse_qs(body, keep_blank_values=True, encoding="gb2312", errors="replace")
        result_values = form.get("result", [])
        if not result_values or not result_values[0]:
            self.send_json(400, "缺少 result 参数")
            return

        result = result_values[0]
        print("--- upload request ---", flush=True)
        print("path: %s" % self.path, flush=True)
        print("raw body: %s" % body, flush=True)
        try:
            fields = parse_result(result)
        except ValueError as exc:
            print("validation error: %s" % exc, flush=True)
            self.send_json(400, "上传数据格式错误: " + str(exc))
            return

        record = {"received_at": datetime.now().astimezone().isoformat(timespec="seconds"),
                  "path": self.path, "cmd": fields["cmd"], "username": fields["username"],
                  "password": fields["password"], "items": fields["items"], "result": result,
                  "raw_body": body}
        with UPLOADS_LOCK:
            UPLOADS.append(record)
            upload_number = len(UPLOADS)
        print("upload count: %d" % upload_number, flush=True)
        print("cmd: %s" % fields["cmd"], flush=True)
        print("result: %s" % result, flush=True)
        self.send_json(200, "上传成功")

def main():
    parser = argparse.ArgumentParser(description="Mock server for PesticideTestActivity2 uploads")
    parser.add_argument("--host", default="0.0.0.0", help="bind address (default: 0.0.0.0)")
    parser.add_argument("--port", type=int, default=8080, help="bind port (default: 8080)")
    args = parser.parse_args()
    server = ThreadingHTTPServer((args.host, args.port), UploadHandler)
    print("Mock upload server listening on http://%s:%d/" % (args.host, args.port), flush=True)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nStopping mock upload server", flush=True)
    finally:
        server.server_close()


if __name__ == "__main__":
    main()

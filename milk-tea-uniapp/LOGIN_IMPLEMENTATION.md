# 小程序登录功能实现说明

## 当前状态

### 前端（UniApp）
登录页面已完整实现，支持：
- ✅ 手机号+验证码登录
- ✅ 微信一键登录
- ✅ 游客登录
- ✅ 用户协议确认
- ✅ 美观的UI设计

**文件位置**: `src/pages/login/login.vue`

### 后端（Spring Boot）
当前只支持：
- ✅ 用户名+密码登录
- ✅ 用户注册

**文件位置**: `src/main/java/com/milktea/controller/AuthController.java`

## 需要补充的后端功能

### 1. 手机号+验证码登录

#### 1.1 发送验证码接口

**接口**: `POST /auth/send-code`

**请求参数**:
```json
{
  "phone": "13800138000"
}
```

**响应**:
```json
{
  "code": 200,
  "msg": "验证码已发送",
  "data": null
}
```

**实现要点**:
- 验证手机号格式
- 生成6位随机验证码
- 存储到Redis（有效期5分钟）
- 调用短信服务发送（阿里云/腾讯云）
- 限制发送频率（60秒一次）
- 限制每日发送次数（防止恶意攻击）

**示例代码**:
```java
@PostMapping("/send-code")
public Result<String> sendCode(@RequestBody Map<String, String> request) {
    String phone = request.get("phone");
    
    // 验证手机号
    if (!validatePhone(phone)) {
        return Result.error("手机号格式不正确");
    }
    
    // 检查发送频率
    String rateKey = "sms:rate:" + phone;
    if (redisTemplate.hasKey(rateKey)) {
        return Result.error("请60秒后再试");
    }
    
    // 生成验证码
    String code = generateCode();
    
    // 存储到Redis
    String codeKey = "sms:code:" + phone;
    redisTemplate.opsForValue().set(codeKey, code, 5, TimeUnit.MINUTES);
    redisTemplate.opsForValue().set(rateKey, "1", 60, TimeUnit.SECONDS);
    
    // 发送短信
    smsService.sendCode(phone, code);
    
    return Result.success("验证码已发送");
}
```

#### 1.2 手机号登录接口

**接口**: `POST /auth/phone-login`

**请求参数**:
```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

**响应**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "phone": "13800138000",
      "nickname": "用户138****8000",
      "avatar": "",
      "memberLevel": 0,
      "points": 0,
      "balance": 0
    }
  }
}
```

**实现要点**:
- 验证验证码是否正确
- 验证码验证后立即删除
- 如果用户不存在，自动注册
- 生成JWT token
- 返回用户信息

**示例代码**:
```java
@PostMapping("/phone-login")
public Result<Map<String, Object>> phoneLogin(@RequestBody Map<String, String> request) {
    String phone = request.get("phone");
    String code = request.get("code");
    
    // 验证验证码
    String codeKey = "sms:code:" + phone;
    String savedCode = redisTemplate.opsForValue().get(codeKey);
    
    if (savedCode == null) {
        return Result.error("验证码已过期");
    }
    
    if (!savedCode.equals(code)) {
        return Result.error("验证码错误");
    }
    
    // 删除验证码
    redisTemplate.delete(codeKey);
    
    // 查找或创建用户
    User user = userService.findByPhone(phone);
    if (user == null) {
        user = userService.createByPhone(phone);
    }
    
    // 生成token
    String token = jwtUtil.generateToken(user.getId());
    
    // 返回结果
    Map<String, Object> data = new HashMap<>();
    data.put("token", token);
    data.put("userInfo", user);
    
    return Result.success(data);
}
```

### 2. 微信登录

#### 2.1 微信登录接口

**接口**: `POST /auth/wx-login`

**请求参数**:
```json
{
  "code": "wx_code_from_wechat",
  "userInfo": {
    "nickName": "微信用户",
    "avatarUrl": "https://...",
    "gender": 1,
    "country": "中国",
    "province": "广东",
    "city": "深圳"
  },
  "rawData": "...",
  "signature": "...",
  "encryptedData": "...",
  "iv": "..."
}
```

**响应**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userInfo": {
      "id": 1,
      "openid": "oxxxxxxxxxxxxxx",
      "nickname": "微信用户",
      "avatar": "https://...",
      "memberLevel": 0,
      "points": 0,
      "balance": 0
    }
  }
}
```

**实现要点**:
- 调用微信API获取session_key和openid
- 验证用户信息签名
- 根据openid查找或创建用户
- 更新用户信息（昵称、头像等）
- 生成JWT token

**示例代码**:
```java
@PostMapping("/wx-login")
public Result<Map<String, Object>> wxLogin(@RequestBody Map<String, Object> request) {
    String code = (String) request.get("code");
    Map<String, Object> userInfo = (Map<String, Object>) request.get("userInfo");
    
    // 调用微信API获取openid
    WxSession wxSession = wechatService.code2Session(code);
    String openid = wxSession.getOpenid();
    String sessionKey = wxSession.getSessionKey();
    
    // 验证签名
    String rawData = (String) request.get("rawData");
    String signature = (String) request.get("signature");
    if (!wechatService.verifySignature(rawData, sessionKey, signature)) {
        return Result.error("签名验证失败");
    }
    
    // 查找或创建用户
    User user = userService.findByOpenid(openid);
    if (user == null) {
        user = userService.createByWechat(openid, userInfo);
    } else {
        // 更新用户信息
        userService.updateWechatInfo(user.getId(), userInfo);
    }
    
    // 生成token
    String token = jwtUtil.generateToken(user.getId());
    
    // 返回结果
    Map<String, Object> data = new HashMap<>();
    data.put("token", token);
    data.put("userInfo", user);
    
    return Result.success(data);
}
```

**微信配置**:
```yaml
wechat:
  miniapp:
    appid: your_appid
    secret: your_secret
```

### 3. Token验证接口

**接口**: `GET /auth/validate`

**请求头**:
```
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "msg": "token有效",
  "data": {
    "userId": 1,
    "valid": true
  }
}
```

### 4. 退出登录接口

**接口**: `POST /auth/logout`

**请求头**:
```
Authorization: Bearer {token}
```

**响应**:
```json
{
  "code": 200,
  "msg": "退出成功",
  "data": null
}
```

## 数据库表设计

### 用户表 (user)
```sql
ALTER TABLE `user` ADD COLUMN `openid` VARCHAR(100) DEFAULT NULL COMMENT '微信openid';
ALTER TABLE `user` ADD COLUMN `session_key` VARCHAR(100) DEFAULT NULL COMMENT '微信session_key';
ALTER TABLE `user` ADD COLUMN `union_id` VARCHAR(100) DEFAULT NULL COMMENT '微信unionid';
ALTER TABLE `user` ADD COLUMN `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间';
ALTER TABLE `user` ADD COLUMN `last_login_ip` VARCHAR(50) DEFAULT NULL COMMENT '最后登录IP';

ALTER TABLE `user` ADD UNIQUE KEY `uk_openid` (`openid`);
```

## 前端修改建议

### 1. 修改API调用

当前登录页面调用的是：
```javascript
api.auth.login({ phone, code })  // 手机号登录
api.auth.wxLogin({ code, userInfo, ... })  // 微信登录
```

需要确保后端接口路径匹配：
- `/auth/phone-login` 或 `/auth/login` (需要后端支持phone+code)
- `/auth/wx-login`

### 2. 发送验证码功能

当前代码中发送验证码是注释掉的：
```javascript
// 第267行
// const res = await api.auth.sendCode({ phone: this.phoneForm.phone })
```

取消注释并确保后端接口可用。

### 3. 错误处理

增强错误处理，区分不同的错误类型：
```javascript
catch (error) {
  let message = '登录失败'
  
  if (error.code === 401) {
    message = '验证码错误或已过期'
  } else if (error.code === 429) {
    message = '操作过于频繁，请稍后再试'
  } else if (error.message) {
    message = error.message
  }
  
  uni.showToast({
    title: message,
    icon: 'none'
  })
}
```

## 安全建议

1. **验证码安全**
   - 限制发送频率（60秒/次）
   - 限制每日发送次数（10次/天）
   - 验证码有效期5分钟
   - 验证后立即删除

2. **Token安全**
   - 使用HTTPS传输
   - Token有效期7天
   - 支持刷新Token机制
   - 敏感操作需要二次验证

3. **防刷机制**
   - IP限流
   - 设备指纹识别
   - 异常登录检测
   - 验证码图形验证

4. **数据加密**
   - 密码使用BCrypt加密
   - 敏感信息传输加密
   - Session Key安全存储

## 测试清单

- [ ] 手机号格式验证
- [ ] 验证码发送成功
- [ ] 验证码60秒限制
- [ ] 验证码正确性验证
- [ ] 验证码过期处理
- [ ] 新用户自动注册
- [ ] 老用户正常登录
- [ ] Token生成和验证
- [ ] 微信登录流程
- [ ] 游客模式
- [ ] 协议确认
- [ ] 退出登录
- [ ] Token过期处理

## 开发优先级

1. **高优先级**（必须实现）
   - 手机号+验证码登录
   - 发送验证码接口
   - Token验证

2. **中优先级**（建议实现）
   - 微信登录
   - 退出登录
   - 用户信息更新

3. **低优先级**（可选）
   - 游客模式完善
   - 第三方登录（QQ、支付宝）
   - 生物识别登录

## 相关文档

- [微信小程序登录文档](https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/login.html)
- [JWT使用指南](https://jwt.io/)
- [短信服务接入](https://help.aliyun.com/product/44282.html)

---

**最后更新**: 2025-11-20
**维护者**: 开发团队

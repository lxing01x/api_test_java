# API 自动化测试框架

## 项目简介

这是一个基于 Java + Groovy 语言，使用 Spock + RestAssured 构建的接口自动化测试框架。项目采用 Gradle 进行依赖管理，具有清晰的分层架构设计。

## 技术栈

- **编程语言**: Java 11 + Groovy 3.0
- **测试框架**: Spock Framework 2.3
- **API 测试库**: RestAssured 5.3
- **构建工具**: Gradle
- **数据处理**: Jackson
- **日志框架**: SLF4J + Logback
- **工具类**: Lombok, Apache Commons

## 项目结构

```
api-automation-framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/automation/
│   │   │       ├── config/          # 配置层
│   │   │       │   └── EnvironmentConfig.java
│   │   │       ├── core/            # 核心层
│   │   │       │   ├── RestAssuredManager.java
│   │   │       │   ├── ResponseHandler.java
│   │   │       │   ├── TestContext.java
│   │   │       │   └── RetryHelper.java
│   │   │       ├── model/           # 数据模型层
│   │   │       │   ├── ApiResponse.java
│   │   │       │   ├── AuthRequest.java
│   │   │       │   ├── AuthResponse.java
│   │   │       │   └── User.java
│   │   │       ├── service/         # 业务服务层
│   │   │       │   ├── BaseService.java
│   │   │       │   ├── AuthService.java
│   │   │       │   └── UserService.java
│   │   │       └── utils/           # 工具类
│   │   │           ├── DateUtils.java
│   │   │           ├── DataGenerator.java
│   │   │           ├── JsonUtils.java
│   │   │           └── YamlUtils.java
│   │   └── resources/
│   └── test/
│       ├── groovy/
│       │   └── com/automation/
│       │       ├── core/
│       │       │   └── BaseSpec.groovy      # Spock 测试基类
│       │       └── specs/
│       │           ├── AuthSpec.groovy       # 认证接口测试
│       │           ├── UserSpec.groovy       # 用户管理接口测试
│       │           └── DemoSpec.groovy       # 功能演示测试
│       └── resources/
│           ├── config/
│           │   ├── common.yaml      # 公共配置
│           │   ├── dev.yaml         # 开发环境配置
│           │   ├── test.yaml        # 测试环境配置
│           │   └── prod.yaml        # 生产环境配置
│           └── logback.xml          # 日志配置
├── build.gradle
├── settings.gradle
└── README.md
```

## 分层架构设计

### 1. 配置层 (Config Layer)

**职责**: 管理多环境配置，支持开发、测试、生产环境切换。

**核心类**:
- `EnvironmentConfig`: 配置管理类，支持 YAML 配置文件加载

**使用方式**:
```java
// 获取当前环境
String env = EnvironmentConfig.getCurrentEnv();

// 获取配置值
String baseUrl = EnvironmentConfig.getBaseUrl();
int timeout = EnvironmentConfig.getTimeout();
String username = EnvironmentConfig.getString("auth.username");
```

### 2. 核心层 (Core Layer)

**职责**: 封装 RestAssured，提供统一的请求发送、响应处理、测试上下文管理。

**核心类**:

#### RestAssuredManager
统一管理 RestAssured 配置，提供预设的请求规范。

```java
// 获取基础请求规范
RequestSpecification spec = RestAssuredManager.given();

// 获取带认证的请求规范
RequestSpecification authSpec = RestAssuredManager.givenWithAuth(token);

// 设置基础 URL
RestAssuredManager.setBaseUri("https://api.example.com");
```

#### ResponseHandler
封装响应处理，提供统一的断言方法。

```java
ResponseHandler response = new ResponseHandler(httpResponse);

// 获取响应信息
int statusCode = response.getStatusCode();
String body = response.getBody();
String header = response.getHeader("Content-Type");

// 响应断言
response.validateStatusCode(200)
        .validateStatusCodeIsSuccess()
        .validateJsonPath("data.name", "test")
        .validateHeader("Content-Type", "application/json")
        .validateResponseTime(5000);
```

#### TestContext
线程安全的测试上下文，用于测试用例间的数据共享。

```java
// 存储数据
TestContext.set("userId", 12345);
TestContext.set("authToken", "Bearer xxxxx");

// 获取数据
Long userId = TestContext.get("userId");
String token = TestContext.get("authToken");

// 获取默认值
String value = TestContext.getOrDefault("key", "defaultValue");

// 清除上下文
TestContext.clear();
```

#### RetryHelper
提供重试机制，处理接口不稳定的情况。

```java
// 简单重试
String result = RetryHelper.retry(() -> {
    return callUnstableApi();
}, 3, 1000); // 最多重试3次，间隔1秒

// 带条件验证的重试
String result = RetryHelper.retryWithCondition(
    () -> callApi(),
    response -> response.getStatusCode() == 200,
    3, 1000
);
```

### 3. 数据模型层 (Model Layer)

**职责**: 定义请求/响应的数据模型，使用 Lombok 简化代码。

**核心模型**:
- `ApiResponse<T>`: 通用 API 响应包装
- `AuthRequest`: 认证请求体
- `AuthResponse`: 认证响应体
- `User`: 用户模型

### 4. 业务服务层 (Service Layer)

**职责**: 封装具体的接口调用，提供业务层面的方法。

**核心类**:

#### BaseService
所有服务类的基类，提供通用的 HTTP 方法封装。

```java
public class MyService extends BaseService {
    public MyService() {
        super("/api/v1/my-resource");
    }
    
    public ResponseHandler getById(Long id) {
        return get("/{id}", id);
    }
    
    public ResponseHandler create(Object request) {
        return post("", request);
    }
}
```

#### AuthService
认证相关接口封装。

```java
AuthService authService = new AuthService();

// 登录
AuthRequest request = AuthRequest.builder()
    .username("user")
    .password("pass")
    .build();
AuthResponse response = authService.loginAndGetToken(request);

// 登出
authService.logout();

// 获取当前用户
ResponseHandler user = authService.getCurrentUser();
```

#### UserService
用户管理接口封装。

```java
UserService userService = new UserService();

// 创建用户
User user = User.builder()
    .username("testuser")
    .email("test@example.com")
    .password("Test123456")
    .build();
User createdUser = userService.createUser(user);

// 查询用户
ResponseHandler response = userService.getUserById(123L);

// 更新用户
userService.updateUser(123L, updatedUser);

// 删除用户
userService.deleteUser(123L);
```

### 5. 测试层 (Test Layer)

**职责**: 使用 Spock 编写测试用例，基于 Given-When-Then 风格。

#### BaseSpec
所有测试类的基类，提供 setup/cleanup 生命周期管理。

```groovy
class MySpec extends BaseSpec {
    
    def "测试接口功能"() {
        given: "准备测试数据"
        def request = [name: "test"]
        
        when: "调用接口"
        ResponseHandler response = post("/api/endpoint", request)
        
        then: "验证结果"
        response.statusCode == 200
        response.getJsonPath("success") == true
    }
}
```

#### Spock 测试特性

**数据驱动测试**:
```groovy
@Unroll
def "测试登录参数验证 - #param: #value"() {
    given: "准备请求"
    def request = new AuthRequest(param, value)
    
    when: "调用登录"
    def response = authService.login(request)
    
    then: "验证响应"
    response.statusCode == expectedCode
    
    where:
    param       | value   || expectedCode
    "username"  | ""      || 400
    "password"  | ""      || 400
    "username"  | null    || 400
}
```

**标签和忽略**:
```groovy
@Title("测试套件名称")
@Narrative("测试套件描述")
class MySpec extends BaseSpec {
    
    @Ignore("暂时跳过")
    def "被忽略的测试"() {
        expect:
        true
    }
}
```

### 6. 工具类层 (Utils Layer)

#### DataGenerator
数据生成工具。

```java
// UUID
String uuid = DataGenerator.generateUUID();

// 随机字符串
String str = DataGenerator.generateRandomString(10);

// 随机数字
String num = DataGenerator.generateRandomNumeric(6);

// 邮箱
String email = DataGenerator.generateEmail("user");

// 手机号
String phone = DataGenerator.generatePhoneNumber();

// 随机整数
int i = DataGenerator.generateRandomInt(1, 100);

// 随机列表选择
String selected = DataGenerator.randomFromList("A", "B", "C");
```

#### DateUtils
日期工具。

```java
// 当前日期时间
String now = DateUtils.getCurrentDateTime();  // yyyy-MM-dd HH:mm:ss
String date = DateUtils.getCurrentDate();    // yyyy-MM-dd
String time = DateUtils.getCurrentTime();    // HH:mm:ss

// 日期偏移
String future = DateUtils.addDays(7);      // 7天后
String past = DateUtils.subtractDays(7);   // 7天前
String later = DateUtils.addHours(24);     // 24小时后

// 自定义格式
String custom = DateUtils.getCurrentDateTime("MM/dd/yyyy");
```

#### JsonUtils
JSON 处理工具。

```java
// 对象转 JSON
String json = JsonUtils.toJson(object);
String pretty = JsonUtils.toPrettyJson(object);

// JSON 转对象
MyObject obj = JsonUtils.fromJson(jsonString, MyObject.class);

// JSON 转 Map
Map<String, Object> map = JsonUtils.toMap(jsonString);

// 按路径获取值
String value = JsonUtils.getValueByPath(json, "user.contacts.0.email");

// 合并 JSON
String merged = JsonUtils.merge(json1, json2);

// 验证 JSON
boolean valid = JsonUtils.isValidJson(jsonString);
```

#### YamlUtils
YAML 处理工具。

```java
// 加载 YAML 文件
Map<String, Object> config = YamlUtils.loadYamlFile("config.yaml");

// 从 classpath 加载
Map<String, Object> config = YamlUtils.loadYamlFromClasspath("config/dev.yaml");

// YAML 与 JSON 互转
String json = YamlUtils.yamlToJson(yamlString);
String yaml = YamlUtils.jsonToYaml(jsonString);
```

## 环境配置

### 配置文件结构

配置文件位于 `src/test/resources/config/` 目录：

- `common.yaml` - 公共配置，所有环境共享
- `dev.yaml` - 开发环境配置
- `test.yaml` - 测试环境配置  
- `prod.yaml` - 生产环境配置

### 配置示例

**common.yaml**:
```yaml
common:
  version: "1.0.0"
  author: "Automation Team"

headers:
  common:
    Content-Type: "application/json"
    Accept: "application/json"
    User-Agent: "Automation-Framework/1.0"
```

**dev.yaml**:
```yaml
api:
  baseUrl: "https://dev-api.example.com"
  timeout: 30000
  retryCount: 3

auth:
  username: "dev_user"
  password: "dev_password_123"
  tokenEndpoint: "/api/v1/auth/login"

database:
  host: "localhost"
  port: 3306
  name: "test_db"
```

### 环境切换

运行时通过 `env` 系统属性指定环境：

```bash
# 使用开发环境（默认）
./gradlew test

# 使用测试环境
./gradlew test -Denv=test

# 使用生产环境
./gradlew test -Denv=prod
```

## 快速开始

### 环境要求

- JDK 11+
- Gradle 7.0+（或使用 Gradle Wrapper）

### 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行指定测试类
./gradlew test --tests "com.automation.specs.AuthSpec"

# 运行指定测试方法
./gradlew test --tests "com.automation.specs.AuthSpec.*登录成功*"

# 指定环境运行
./gradlew test -Denv=test

# 生成测试报告
./gradlew clean test
```

### 测试报告

测试报告位于 `build/reports/tests/test/` 目录：

- HTML 报告: `index.html`
- JUnit XML: `*.xml`

## 最佳实践

### 1. 测试用例设计

- **一个测试用例只验证一个场景**
- **使用 Given-When-Then 三段式结构**
- **保持测试用例独立，不依赖执行顺序**
- **使用数据驱动测试覆盖边界条件**

### 2. 断言策略

- **优先使用 ResponseHandler 的断言方法**
- **验证状态码、响应时间、关键数据**
- **避免过度断言，只验证业务关心的字段**

### 3. 数据管理

- **使用 DataGenerator 生成测试数据**
- **测试数据用完后清理**
- **不依赖现有数据，确保测试可重复执行**

### 4. 日志记录

- **关键步骤使用 log 记录**
- **请求响应自动由 RestAssured 记录**
- **日志级别可通过 logback.xml 调整**

## 扩展指南

### 添加新的 Service 类

```java
public class ProductService extends BaseService {
    
    public ProductService() {
        super("/api/v1/products");
    }
    
    public ResponseHandler getProduct(Long id) {
        return get("/{id}", id);
    }
    
    public ResponseHandler createProduct(Product product) {
        return post("", product);
    }
}
```

### 添加新的测试用例

```groovy
class ProductSpec extends BaseSpec {
    
    ProductService productService = new ProductService()
    
    def setup() {
        // 设置认证 token
        productService.setAuthToken(TestContext.get("authToken"))
    }
    
    def "测试创建产品"() {
        given: "准备产品数据"
        def product = Product.builder()
            .name("Test Product")
            .price(new BigDecimal("99.99"))
            .build()
        
        when: "创建产品"
        def response = productService.createProduct(product)
        
        then: "验证结果"
        response.statusCode == 201
        response.getJsonPath("name") == "Test Product"
    }
}
```

### 自定义配置

在 `src/test/resources/config/` 下添加新的环境配置文件，然后使用 `-Denv=xxx` 指定。

## 常见问题

### Q: 如何处理接口依赖？

A: 使用 `TestContext` 在测试用例间传递数据，或在 `setup()` 方法中准备前置数据。

### Q: 如何处理接口不稳定？

A: 使用 `RetryHelper` 添加重试机制，或在测试基类中统一配置重试策略。

### Q: 如何调试接口请求？

A: RestAssured 会自动记录请求响应日志，也可以在测试中使用 `response.prettyPrint()` 打印响应体。

### Q: 如何并行执行测试？

A: 修改 `build.gradle` 中的 test 配置，添加 `maxParallelForks` 设置。

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request。

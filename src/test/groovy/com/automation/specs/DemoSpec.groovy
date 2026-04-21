package com.automation.specs

import com.automation.core.BaseSpec
import com.automation.core.ResponseHandler
import com.automation.core.TestContext
import com.automation.utils.DataGenerator
import com.automation.utils.DateUtils
import com.automation.utils.JsonUtils
import spock.lang.Ignore
import spock.lang.Narrative
import spock.lang.Title
import spock.lang.Unroll

@Title("基础功能演示测试")
@Narrative("展示框架基本功能的测试用例，包括工具类使用、数据生成等")
class DemoSpec extends BaseSpec {

    def "测试数据生成工具 - UUID"() {
        when: "生成UUID"
        def uuid = DataGenerator.generateUUID()

        then: "验证UUID格式"
        uuid != null
        uuid.length() == 36
        uuid.contains("-")
    }

    def "测试数据生成工具 - 随机字符串"() {
        given: "指定长度"
        int length = 10

        when: "生成随机字符串"
        def randomString = DataGenerator.generateRandomString(length)

        then: "验证随机字符串"
        randomString != null
        randomString.length() == length
        randomString ==~ /[a-z]+/
    }

    def "测试数据生成工具 - 邮箱"() {
        given: "邮箱前缀"
        def prefix = "test"

        when: "生成邮箱"
        def email = DataGenerator.generateEmail(prefix)

        then: "验证邮箱格式"
        email != null
        email.startsWith(prefix.toLowerCase())
        email.contains("@")
        email.endsWith("@example.com")
    }

    def "测试数据生成工具 - 手机号"() {
        when: "生成手机号"
        def phone = DataGenerator.generatePhoneNumber()

        then: "验证手机号格式"
        phone != null
        phone.length() == 11
        phone.startsWith("1")
        phone ==~ /\d+/
    }

    def "测试日期工具 - 当前日期"() {
        when: "获取当前日期"
        def currentDate = DateUtils.getCurrentDate()

        then: "验证日期格式"
        currentDate != null
        currentDate ==~ /\d{4}-\d{2}-\d{2}/
    }

    def "测试日期工具 - 当前时间"() {
        when: "获取当前时间"
        def currentTime = DateUtils.getCurrentTime()

        then: "验证时间格式"
        currentTime != null
        currentTime ==~ /\d{2}:\d{2}:\d{2}/
    }

    def "测试日期工具 - 日期偏移"() {
        given: "偏移天数"
        int days = 7

        when: "获取偏移后的日期"
        def futureDate = DateUtils.addDays(days)

        then: "验证日期格式"
        futureDate != null
        futureDate ==~ /\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}/
    }

    def "测试JSON工具 - 对象转JSON"() {
        given: "测试对象"
        def testObject = [
            name: "test",
            value: 123,
            active: true
        ]

        when: "转换为JSON"
        def json = JsonUtils.toJson(testObject)

        then: "验证JSON"
        json != null
        JsonUtils.isValidJson(json)
    }

    def "测试JSON工具 - JSON转Map"() {
        given: "JSON字符串"
        def json = '{"name":"test","value":123}'

        when: "转换为Map"
        def map = JsonUtils.toMap(json)

        then: "验证Map"
        map != null
        map.name == "test"
        map.value == 123
    }

    def "测试JSON工具 - 按路径获取值"() {
        given: "JSON字符串"
        def json = '''
        {
            "user": {
                "name": "John",
                "contacts": [
                    {"type": "email", "value": "john@example.com"},
                    {"type": "phone", "value": "123456789"}
                ]
            }
        }
        '''

        expect: "按路径获取值"
        JsonUtils.getValueByPath(json, "user.name") == "John"
        JsonUtils.getValueByPath(json, "user.contacts.0.type") == "email"
        JsonUtils.getValueByPath(json, "user.contacts.1.value") == "123456789"
    }

    def "测试测试上下文 - 存储和获取数据"() {
        given: "测试数据"
        def key = "testKey"
        def value = "testValue"

        when: "存储到上下文"
        TestContext.set(key, value)

        then: "从上下文获取"
        TestContext.get(key) == value
        TestContext.containsKey(key)
    }

    def "测试测试上下文 - 不存在的键"() {
        given: "不存在的键"
        def nonExistentKey = "nonExistentKey"

        expect: "验证行为"
        TestContext.get(nonExistentKey) == null
        !TestContext.containsKey(nonExistentKey)
        TestContext.getOrDefault(nonExistentKey, "default") == "default"
    }

    @Unroll
    def "测试随机整数生成 - 范围 [#min, #max]"() {
        when: "生成随机整数"
        def result = DataGenerator.generateRandomInt(min, max)

        then: "验证结果在范围内"
        result >= min
        result <= max

        where:
        min | max
        0   | 10
        1   | 100
        -50 | 50
        100 | 200
    }

    @Ignore("演示忽略测试用例")
    def "被忽略的测试用例"() {
        expect:
        true
    }
}

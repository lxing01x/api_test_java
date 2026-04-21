package com.automation.specs

import com.automation.core.BaseSpec
import com.automation.core.ResponseHandler
import com.automation.service.AuthService
import com.automation.model.AuthRequest
import com.automation.utils.DataGenerator
import spock.lang.Narrative
import spock.lang.Title
import spock.lang.Unroll

@Title("认证接口测试套件")
@Narrative("测试用户认证相关的接口，包括登录、登出、令牌刷新等")
class AuthSpec extends BaseSpec {

    AuthService authService

    def setup() {
        authService = new AuthService()
    }

    def "测试用户登录成功"() {
        given: "准备有效的登录请求"
        def authRequest = AuthRequest.builder()
                .username("test_user")
                .password("test_password")
                .build()

        when: "调用登录接口"
        ResponseHandler response = authService.login(authRequest)

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("token") != null
        response.getJsonPath("user.username") == "test_user"
    }

    def "测试用户登录失败 - 密码错误"() {
        given: "准备错误的登录请求"
        def authRequest = AuthRequest.builder()
                .username("test_user")
                .password("wrong_password")
                .build()

        when: "调用登录接口"
        ResponseHandler response = authService.login(authRequest)

        then: "验证响应"
        response.statusCode == 401
        response.getJsonPath("message") == "Invalid credentials"
    }

    def "测试用户登录失败 - 用户不存在"() {
        given: "准备不存在的用户请求"
        def authRequest = AuthRequest.builder()
                .username("non_existent_user_" + DataGenerator.generateRandomNumeric(6))
                .password("any_password")
                .build()

        when: "调用登录接口"
        ResponseHandler response = authService.login(authRequest)

        then: "验证响应"
        response.statusCode == 404
        response.getJsonPath("message").contains("User not found")
    }

    @Unroll
    def "测试登录参数验证 - 参数：#param，值：#value"() {
        given: "准备无效的登录请求"
        def authRequest = AuthRequest.builder()
                .username(param == "username" ? value : "test_user")
                .password(param == "password" ? value : "test_password")
                .build()

        when: "调用登录接口"
        ResponseHandler response = authService.login(authRequest)

        then: "验证响应"
        response.statusCode == expectedStatusCode

        where:
        param       | value   || expectedStatusCode
        "username"  | ""      || 400
        "password"  | ""      || 400
        "username"  | null    || 400
        "password"  | null    || 400
    }

    def "测试用户登出"() {
        given: "用户已登录"
        def authRequest = AuthRequest.builder()
                .username("test_user")
                .password("test_password")
                .build()
        authService.loginAndGetToken(authRequest)

        when: "调用登出接口"
        ResponseHandler response = authService.logout()

        then: "验证响应"
        response.statusCode == 200
    }

    def "测试获取当前用户信息"() {
        given: "用户已登录"
        def authRequest = AuthRequest.builder()
                .username("test_user")
                .password("test_password")
                .build()
        authService.loginAndGetToken(authRequest)

        when: "调用获取当前用户接口"
        ResponseHandler response = authService.getCurrentUser()

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("username") == "test_user"
    }

    def "测试未登录访问受保护接口"() {
        when: "不登录直接访问受保护接口"
        ResponseHandler response = authService.getCurrentUser()

        then: "验证响应"
        response.statusCode == 401
    }
}

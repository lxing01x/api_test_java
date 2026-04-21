package com.automation.specs

import com.automation.core.BaseSpec
import com.automation.core.ResponseHandler
import com.automation.core.TestContext
import com.automation.service.AuthService
import com.automation.service.UserService
import com.automation.model.AuthRequest
import com.automation.model.User
import com.automation.utils.DataGenerator
import spock.lang.Narrative
import spock.lang.Shared
import spock.lang.Title

@Title("用户管理接口测试套件")
@Narrative("测试用户管理相关的接口，包括创建、查询、更新、删除用户等")
class UserSpec extends BaseSpec {

    @Shared
    AuthService authService = new AuthService()
    
    @Shared
    UserService userService = new UserService()
    
    @Shared
    User createdUser

    def setupSpec() {
        def authRequest = AuthRequest.builder()
                .username("admin_user")
                .password("admin_password")
                .build()
        
        authService.loginAndGetToken(authRequest)
        userService.setAuthToken(TestContext.get("authToken"))
    }

    def "测试创建用户"() {
        given: "准备用户数据"
        def username = "test_user_" + DataGenerator.generateRandomNumeric(6)
        def email = DataGenerator.generateEmail("test")
        
        def user = User.builder()
                .username(username)
                .email(email)
                .password("Test123456")
                .firstName("Test")
                .lastName("User")
                .phone(DataGenerator.generatePhoneNumber())
                .status(User.UserStatus.ACTIVE)
                .build()

        when: "调用创建用户接口"
        createdUser = userService.createUser(user)

        then: "验证响应"
        createdUser.id != null
        createdUser.username == username
        createdUser.email == email
    }

    def "测试根据ID获取用户"() {
        given: "已创建的用户ID"
        def userId = createdUser.id

        when: "调用获取用户接口"
        ResponseHandler response = userService.getUserById(userId)

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("id") == userId
        response.getJsonPath("username") == createdUser.username
    }

    def "测试获取所有用户"() {
        when: "调用获取所有用户接口"
        ResponseHandler response = userService.getAllUsers()

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("content") != null
    }

    def "测试分页获取用户"() {
        given: "分页参数"
        int page = 0
        int size = 10

        when: "调用分页获取用户接口"
        ResponseHandler response = userService.getAllUsers(page, size)

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("size") == size
    }

    def "测试更新用户信息"() {
        given: "更新后的用户数据"
        def updatedEmail = "updated_" + DataGenerator.generateEmail("user")
        
        createdUser.email = updatedEmail
        createdUser.firstName = "UpdatedFirstName"

        when: "调用更新用户接口"
        ResponseHandler response = userService.updateUser(createdUser.id, createdUser)

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("email") == updatedEmail
        response.getJsonPath("firstName") == "UpdatedFirstName"
    }

    def "测试更新用户状态"() {
        given: "新的用户状态"
        def newStatus = User.UserStatus.INACTIVE

        when: "调用更新用户状态接口"
        ResponseHandler response = userService.updateUserStatus(createdUser.id, newStatus)

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("status") == newStatus.name()
    }

    def "测试搜索用户"() {
        given: "搜索关键词"
        def keyword = createdUser.username.substring(0, 5)

        when: "调用搜索用户接口"
        ResponseHandler response = userService.searchUsers(keyword)

        then: "验证响应"
        response.statusCode == 200
        response.getJsonPath("content") != null
    }

    def "测试删除用户"() {
        given: "要删除的用户ID"
        def userId = createdUser.id

        when: "调用删除用户接口"
        ResponseHandler response = userService.deleteUser(userId)

        then: "验证响应"
        response.statusCode == 204
    }

    def "测试获取已删除的用户"() {
        given: "已删除的用户ID"
        def userId = createdUser.id

        when: "调用获取用户接口"
        ResponseHandler response = userService.getUserById(userId)

        then: "验证响应"
        response.statusCode == 404
    }
}

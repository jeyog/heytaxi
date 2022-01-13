package com.moca.heytaxi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moca.heytaxi.domain.User;
import com.moca.heytaxi.dto.UserDTO;
import com.moca.heytaxi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.security.config.BeanIds;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.ServletException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
public class UserControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) throws ServletException {

        DelegatingFilterProxy delegateProxyFilter = new DelegatingFilterProxy();
        delegateProxyFilter.init(new MockFilterConfig(webApplicationContext.getServletContext(), BeanIds.SPRING_SECURITY_FILTER_CHAIN));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .addFilter(delegateProxyFilter)
                .build();
    }

    @Test
    public void loadMe() throws Exception {
        this.mockMvc.perform(get("/api/user").header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwIiwiaWF0IjoxNjQxNzQwNDAwLCJleHAiOjE1NDkyNDA5NjAwfQ.UNSrayKVbrOzpjgavJD1en0nxA_GOj_JYatzL6O25e0")).andExpect(status().isOk())
                .andDo(document("user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING).description("결과 설명"),
                                PayloadDocumentation.fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                                PayloadDocumentation.fieldWithPath("user.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                PayloadDocumentation.fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 전화번호"))));
    }

    @Test
    public void putMe() throws Exception {
        User user = new User();
        user.setId(0L);
        user.setUsername("01012345678");
        user.setName("홍길동");

        UserDTO.Request request = new UserDTO.Request();
        request.setUsername("01012345678");
        request.setName("홍길동");

        when(userService.updateUser(any(User.class))).thenReturn(user);

        this.mockMvc.perform(put("/api/user")
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwIiwiaWF0IjoxNjQxNzQwNDAwLCJleHAiOjE1NDkyNDA5NjAwfQ.UNSrayKVbrOzpjgavJD1en0nxA_GOj_JYatzL6O25e0")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andDo(document("put-user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")
                        ),
                        PayloadDocumentation.requestFields(
                               PayloadDocumentation.fieldWithPath("id").type(JsonFieldType.NUMBER).description("사용자 고유 번호"),
                                PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 전화번호"),
                                PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING).description("결과 설명"),
                                PayloadDocumentation.fieldWithPath("user").type(JsonFieldType.OBJECT).description("사용자 정보"),
                                PayloadDocumentation.fieldWithPath("user.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                PayloadDocumentation.fieldWithPath("user.username").type(JsonFieldType.STRING).description("사용자 전화번호"))));
    }

    @Test
    public void deleteMe() throws Exception {
        this.mockMvc.perform(delete("/api/user").header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIwIiwiaWF0IjoxNjQxNzQwNDAwLCJleHAiOjE1NDkyNDA5NjAwfQ.UNSrayKVbrOzpjgavJD1en0nxA_GOj_JYatzL6O25e0")).andExpect(status().isOk())
                .andDo(document("user",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")
                        ),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING).description("결과 설명"),
                                PayloadDocumentation.fieldWithPath("user").type(JsonFieldType.NULL).description(""))));
    }
}

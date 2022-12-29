package com.finalproject_sns.controller;

import com.finalproject_sns.domain.dto.post.PostResponse;
import com.finalproject_sns.service.HelloService;
import com.finalproject_sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    HelloService helloService;

    @Test
    @DisplayName("이름이 잘 나오는지 테스트")
    @WithMockUser
    void hello_success() throws Exception {

        mockMvc.perform(get("/api/v1/hello")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("합 테스트")
    @WithMockUser
    void sumOfDigit_success() throws Exception {

        when(helloService.sumOfDigit("111")).thenReturn("3");
        mockMvc.perform(get("/api/v1/hello/111")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }
}
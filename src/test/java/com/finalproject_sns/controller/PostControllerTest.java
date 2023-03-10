package com.finalproject_sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject_sns.domain.User;
import com.finalproject_sns.domain.dto.post.*;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.service.PostService;
import com.finalproject_sns.utils.JwtTokenUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
//@MockBean(JpaMetamodelMappingContext.class) -> Application???????????? @EnableJpaAuditing ??????
class PostControllerTest {

    @MockBean
    PostService postService;

    @MockBean
    BCryptPasswordEncoder encoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    MyFeedResponse myFeedResponse = MyFeedResponse.builder()
            .id(1L)
            .title("testTitle")
            .body("testBody")
            .userName("testUser")
            .createAt(LocalDateTime.now())
            .build();

    private String token;
    @Value("${jwt.secret.token}")
    private String secretKey;

    public final LocalDateTime times = LocalDateTime.now();

    @BeforeEach
    void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtTokenUtil.createToken("user1", secretKey, System.currentTimeMillis() + expireTimeMs);
    }


    @Test
    @DisplayName("?????? ??????")
    @WithMockUser  // ?????? ??????
    void post_success() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("??????")
                .body("???????????????")
                .build();

        // any() ?????? ????????? ????????? ????????? result??? null??? ???????????? ??????????
        when(postService.create(any(),any())).thenReturn(PostResponse.builder().message("????????? ????????????").postId(0L).build());
        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("?????? ?????? - ????????? X")
    @WithAnonymousUser // ???????????? ?????? ?????????
    void post_fail1() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("??????")
                .body("???????????????")
                .build();

        when(postService.create(any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"???????????? ?????? ???????????????."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? X(Token X)")
    void post_fail1_noToken() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("??????")
                .body("???????????????")
                .build();

        when(postService.create(any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"????????? ???????????? ????????????."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION,"")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? X(invalid_token)")
    void post_fail_invalid_token() throws Exception {

        User user = User.builder()
                .userName("testUser")
                .password("123")
                .build();

        PostRequest postRequest = PostRequest.builder()
                .title("testTitle")
                .body("???????????????")
                .build();

        token = JwtTokenUtil.createToken(user.getUserName(), secretKey, System.currentTimeMillis());
        when(postService.create(any(),any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN,"???????????? ?????? Token?????????."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION,token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    @WithMockUser // Security ???????????? ????????? ??????
    void post_findOne_success() throws Exception {

        PostSearchResponse postSearchResponse = PostSearchResponse.builder()
                .id(1L)
                .title("test")
                .body("test content")
                .userName("ajin")
                .createAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        when(postService.findOnePost(any())).thenReturn(postSearchResponse);

        mockMvc.perform(get("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postSearchResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value("test"))
                .andExpect(jsonPath("$.result.userName").value("ajin"))
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.createAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ??????")
    @WithMockUser // ?????? ??????
    void post_modify_success() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("????????????")
                .body("?????????????")
                .build();

        when(postService.update(any(), any(), any())).thenReturn(PostResponse.builder()
                        .message("????????? ?????? ??????")
                        .postId(1L)
                .build());

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ?????? - ????????????")
    @WithAnonymousUser // ???????????? ?????? ?????????
    void post_modify_fail1() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("??????")
                .body("???????????????")
                .build();

        when(postService.update(any(),any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"???????????? ?????? ???????????????."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ?????????")
    @WithMockUser
    void post_modify_fail2() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("??????")
                .body("???????????????")
                .build();

        when(postService.update(any(),any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"???????????? ?????? ???????????????."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ??????")
    @WithMockUser // ?????? ??????
    void post_delete_success() throws Exception {


        when(postService.deletePost(any(), any())).thenReturn(PostResponse.builder()
                .message("????????? ?????? ??????")
                .postId(1L)
                .build());

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("?????? ?????? - ?????? ??????")
    @WithAnonymousUser // ?????? ??????
    void post_delete_fail1() throws Exception {

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,""));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ?????????")
    @WithMockUser
    void post_delete_fail2() throws Exception {

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,""));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getHttpStatus().value()));
    }

    // DB ?????? ?????? ??????
    @Test
    @DisplayName("?????? ?????? - ?????????????????? ??????")
    @WithMockUser
    void post_delete_fail3() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "????????? ????????? ??????"));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getHttpStatus().value()));
    }

    @Test
    @DisplayName("?????? ??????")
    @WithMockUser
    void post_list_success() throws Exception {


        mockMvc.perform(get("/api/v1/posts")
                        .param("page","0")
                        .param("size","3")
                        .param("sort","createAt,desc"))
                .andExpect(status().isOk());

        // Pageable ????????? ArgumentCaptor ??????(??????)
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        // ??????
        verify(postService).findAllPost(argumentCaptor.capture());
        // getValue() - ????????? ??? ??????
        PageRequest pageRequest = (PageRequest) argumentCaptor.getValue();

        assertEquals(Sort.by(Sort.Direction.DESC,"createAt"),pageRequest.getSort());
        assertEquals(3,pageRequest.getPageSize());
        assertEquals(0,pageRequest.getPageNumber());
    }


    @Test
    @DisplayName("???????????? ?????? ??????")
    @WithMockUser // ????????? ?????????
    void my_feed_success() throws Exception {

        when(postService.findByUser(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(myFeedResponse)))
                .andExpect(status().isOk())  // 202
                .andDo(print());
    }

    @Test
    @DisplayName("???????????? ?????? ??????")
    @WithAnonymousUser // ???????????? ?????? ?????????(??????)
    void my_feed_fail_not_login() throws Exception {

        when(postService.findByUser(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts/my")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(myFeedResponse)))
                .andExpect(status().isUnauthorized())  // 401
                .andDo(print());

    }
}
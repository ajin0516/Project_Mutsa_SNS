package com.finalproject_sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject_sns.domain.Post;
import com.finalproject_sns.domain.dto.post.PostRequest;
import com.finalproject_sns.domain.dto.post.PostResponse;
import com.finalproject_sns.domain.dto.post.PostSearchResponse;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.exception.UserAppException;
import com.finalproject_sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean
    PostService postService;

    @MockBean
    BCryptPasswordEncoder encoder;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("등록 성공")
    @WithMockUser  // 권한 부여
    void post_success() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        // any() 말고 설정한 변수를 넣으면 result가 null로 넘어오는 이유는?
        when(postService.create(any(),any())).thenReturn(PostResponse.builder().message("포스트 등록완료").postId(0L).build());
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
    @DisplayName("등록 실패 - 로그인 X")
    @WithAnonymousUser // 인증되지 않은 사용자
    void post_fail1() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        when(postService.create(any(),any())).thenThrow(new UserAppException(ErrorCode.INVALID_PERMISSION,"존재하지 않는 회원입니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("단건 조회 성공")
    @WithMockUser // Security 적용해서 없으면 안됨
    void post_findOne_success() throws Exception {

        PostSearchResponse postSearchResponse = PostSearchResponse.builder()
                .id(1L)
                .title("test")
                .body("test content")
                .userName("ajin")
                .createAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        when(postService.findById(any())).thenReturn(postSearchResponse);

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
    @DisplayName("수정 성공")
    @WithMockUser // 권한 부여
    void post_modify_success() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("수정할게")
                .body("수정한다?")
                .build();

        when(postService.update(any(), any(), any())).thenReturn(PostResponse.builder()
                        .message("포스트 수정 완료")
                        .postId(1L)
                .build());

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("수정 실패 - 인증실패")
    @WithAnonymousUser // 인증되지 않은 사용자
    void post_modify_fail1() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        when(postService.update(any(),any(),any())).thenThrow(new UserAppException(ErrorCode.INVALID_PERMISSION,"존재하지 않는 회원입니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("수정 실패 - 작성자 불일치")
    @WithMockUser
    void post_modify_fail2() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        when(postService.update(any(),any(),any())).thenThrow(new UserAppException(ErrorCode.INVALID_PERMISSION,"존재하지 않는 회원입니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("삭제 성공")
    @WithMockUser // 권한 부여
    void post_delete_success() throws Exception {


        when(postService.deletePost(any(), any())).thenReturn(PostResponse.builder()
                .message("포스트 삭제 완료")
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
    @DisplayName("삭제 실패 - 인증 실패")
    @WithAnonymousUser // 권한 없음
    void post_delete_fail1() throws Exception {

        when(postService.deletePost(any(), any())).thenThrow(new UserAppException(ErrorCode.INVALID_PERMISSION,""));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("삭제 실패 - 작성자 불일치")
    @WithMockUser
    void post_delete_fail2() throws Exception {

        when(postService.deletePost(any(), any())).thenThrow(new UserAppException(ErrorCode.INVALID_PERMISSION,""));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getHttpStatus().value()));
    }

    // DB 에서 처리 못함
    @Test
    @DisplayName("삭제 실패 - 데이터베이스 에러")
    @WithMockUser
    void post_delete_fail3() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new UserAppException(ErrorCode.DATABASE_ERROR, "데이터 베이스 에러"));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getHttpStatus().value()));
    }

    // 전체 조회 처리 못함
    @Test
    @DisplayName("조회 성공")
    @WithMockUser
    void post_list_success() throws Exception {

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createAt"));

//        pageRequest.getSort()
//        when(postService.findAllPost(any())).thenReturn()
//                ,post.getUser().getUserName()
//                ,post.getTitle()
//        ,post.getBody()
//        ,post.getCreateAt(),post.getLastModifiedAt());

        mockMvc.perform(get("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(pageRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
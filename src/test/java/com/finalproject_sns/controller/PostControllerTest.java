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
//@MockBean(JpaMetamodelMappingContext.class) -> Application클래스에 @EnableJpaAuditing 적용
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

        when(postService.create(any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"존재하지 않는 회원입니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("등록 실패 - 로그인 X(Token X)")
    void post_fail1_noToken() throws Exception {

        PostRequest postRequest = PostRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        when(postService.create(any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"토큰이 존재하지 않습니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION,"")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("등록 실패 - 로그인 X(invalid_token)")
    void post_fail_invalid_token() throws Exception {

        User user = User.builder()
                .userName("testUser")
                .password("123")
                .build();

        PostRequest postRequest = PostRequest.builder()
                .title("testTitle")
                .body("안녕하세요")
                .build();

        token = JwtTokenUtil.createToken(user.getUserName(), secretKey, System.currentTimeMillis());
        when(postService.create(any(),any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN,"유효하지 않은 Token입니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION,token)
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
    @DisplayName("수정 성공")
    @WithMockUser // 권한 부여
    void post_modify_success() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
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
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("수정 실패 - 인증실패")
    @WithAnonymousUser // 인증되지 않은 사용자
    void post_modify_fail1() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        when(postService.update(any(),any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"존재하지 않는 회원입니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("수정 실패 - 작성자 불일치")
    @WithMockUser
    void post_modify_fail2() throws Exception {

        PostModifyRequest postModifyRequest = PostModifyRequest.builder()
                .title("안녕")
                .body("안녕하세요")
                .build();

        when(postService.update(any(),any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"존재하지 않는 회원입니다."));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postModifyRequest)))
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

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,""));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("삭제 실패 - 작성자 불일치")
    @WithMockUser
    void post_delete_fail2() throws Exception {

        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,""));

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
                .thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "데이터 베이스 에러"));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getHttpStatus().value()));
    }

    @Test
    @DisplayName("조회 성공")
    @WithMockUser
    void post_list_success() throws Exception {


        mockMvc.perform(get("/api/v1/posts")
                        .param("page","0")
                        .param("size","3")
                        .param("sort","createAt,desc"))
                .andExpect(status().isOk());

        // Pageable 타입의 ArgumentCaptor 생성(저장)
        ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

        // 검증
        verify(postService).findAllPost(argumentCaptor.capture());
        // getValue() - 저장한 걸 사용
        PageRequest pageRequest = (PageRequest) argumentCaptor.getValue();

        assertEquals(Sort.by(Sort.Direction.DESC,"createAt"),pageRequest.getSort());
        assertEquals(3,pageRequest.getPageSize());
        assertEquals(0,pageRequest.getPageNumber());
    }


    @Test
    @DisplayName("마이피드 조회 성공")
    @WithMockUser // 인증된 사용자
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
    @DisplayName("마이피드 조회 실패")
    @WithAnonymousUser // 인증되지 않은 사용자(익명)
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
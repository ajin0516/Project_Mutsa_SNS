package com.finalproject_sns.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateRequest;
import com.finalproject_sns.domain.dto.comment.create.CommentCreateResponse;
import com.finalproject_sns.domain.dto.comment.delete.CommentDeleteResponse;
import com.finalproject_sns.domain.dto.comment.list.CommentListResponse;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateRequest;
import com.finalproject_sns.domain.dto.comment.update.CommentUpdateResponse;
import com.finalproject_sns.exception.AppException;
import com.finalproject_sns.exception.ErrorCode;
import com.finalproject_sns.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("?????? ?????? ??????")
    @WithMockUser // ?????? O
    void comment_success() throws Exception {

        when(commentService.create(any(), any(), any())).thenReturn(CommentCreateResponse.builder()
                .id(1L)
                .comment("testComment")
                .userName("testUser")
                .postId(2L)
                .createAt(LocalDateTime.now())
                .build());

        mockMvc.perform(post("/api/v1/posts/2/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentCreateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.createAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ?????? ?????? ??????")
    @WithAnonymousUser
    void comment_fail_not_login() throws Exception {

        when(commentService.create(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ????????? ????????????."));

        mockMvc.perform(post("/api/v1/posts/2/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentCreateRequest())))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - post ??????")
    @WithMockUser
    void comment_fail_no_post() throws Exception {

        when(commentService.create(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "???????????? ?????? ??????????????????."));

        mockMvc.perform(post("/api/v1/posts/2/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentCreateRequest())))
                .andExpect(status().isNotFound())
                .andDo(print());
    }


    @Test
    @DisplayName("?????? ?????? ??????")
    @WithMockUser// ?????? O
    void comment_update_success() throws Exception {

        when(commentService.update(any(), any(), any(), any())).thenReturn(CommentUpdateResponse.builder()
                .id(1L)
                .comment("testComment")
                .userName("testUser")
                .postId(2L)
                .createAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build());

        mockMvc.perform(put("/api/v1/posts/2/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentUpdateRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.comment").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(jsonPath("$.result.createAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? - ?????? ??????")
    @WithAnonymousUser
    void comment_update_fail_unauthorized() throws Exception {

        when(commentService.update(any(), any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"?????? ??????"));

        mockMvc.perform(put("/api/v1/posts/2/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentUpdateRequest())))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? - ?????? ??????")
    @WithMockUser
    void comment_update_fail_no_comment() throws Exception {

        when(commentService.update(any(),any(),any(),any())).thenThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND,"?????? ????????? ???????????? ????????????."));

        mockMvc.perform(put("/api/v1/posts/2/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentUpdateRequest())))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ?????????")
    @WithMockUser
    void comment_update_fail_not_match() throws Exception {
        when(commentService.update(any(),any(),any(),any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION,"????????? ????????? ????????????."));

        mockMvc.perform(put("/api/v1/posts/2/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentUpdateRequest())))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? - ????????? ????????? ??????")
    @WithMockUser
    void comment_update_fail_db() throws Exception {
        when(commentService.update(any(),any(),any(),any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR,"????????? ????????? ???????????????."));

        mockMvc.perform(put("/api/v1/posts/2/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new CommentUpdateRequest())))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class CommentList {

        @Test
        @DisplayName("?????? ?????? ?????? ?????? - ?????? ?????? ??????")
        @WithMockUser
        public void comment_success_list1() throws Exception {

            mockMvc.perform(get("/api/v1/posts/1/comments")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "createAt,desc"))
                    .andExpect(status().isOk());

            // Pageable ????????? ArgumentCaptor ??????(??????)
            ArgumentCaptor<Pageable> argumentCaptor = ArgumentCaptor.forClass(Pageable.class);

            // ??????
            verify(commentService).commentList(any(), argumentCaptor.capture());

            // getValue() - ????????? ??? ??????
            PageRequest pageRequest = (PageRequest) argumentCaptor.getValue();

            assertEquals(Sort.by(Sort.Direction.DESC, "createAt"), pageRequest.getSort());
            assertEquals(0, pageRequest.getPageNumber());
            assertEquals(10, pageRequest.getPageSize());
        }

        @Test
        @DisplayName("?????? ?????? ?????? ?????? - Basic")
        @WithMockUser
        public void comment_success_list2() throws Exception {

            CommentListResponse commentListResponse = CommentListResponse.builder()
                    .id(1L)
                    .postId(1L)
                    .comment("testComment")
                    .userName("testName")
                    .createAt(LocalDateTime.now())
                    .build();

            when(commentService.commentList(any(), any())).thenReturn(Page.empty());

            mockMvc.perform(get("/api/v1/posts/1/comments")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(commentListResponse)))
                    .andExpect(status().isOk())
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    @WithMockUser
    void comment_delete_success() throws Exception {

        CommentDeleteResponse commentDeleteResponse = CommentDeleteResponse.builder().message("?????? ?????? ??????").id(1L).build();

        when(commentService.delete(any(), any(), any())).thenReturn(commentDeleteResponse);

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentDeleteResponse)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ?????? X")
    @WithAnonymousUser
    void comment_delete_fail_Unauthorized() throws Exception {

        when(commentService.delete(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ????????? ????????????."));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ??????")
    @WithMockUser
    void comment_delete_fail_no_Post() throws Exception {

        when(commentService.delete(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, "???????????? ?????? ??????????????????."));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - ????????? ?????????")
    @WithMockUser
    void comment_delete_fail_not_match() throws Exception {

        when(commentService.delete(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, "????????? ????????? ????????????."));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("?????? ?????? ?????? - DB ??????")
    @WithMockUser
    void comment_delete_fail_db() throws Exception {

        when(commentService.delete(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, "????????? ????????? ???????????????."));

        mockMvc.perform(delete("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andDo(print());
    }
}

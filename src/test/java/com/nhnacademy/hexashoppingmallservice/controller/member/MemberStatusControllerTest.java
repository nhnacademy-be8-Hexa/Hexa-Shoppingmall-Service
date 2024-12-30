package com.nhnacademy.hexashoppingmallservice.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.member.MemberStatusRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.exception.SqlQueryExecuteFailException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberStatusNotFoundException;
import com.nhnacademy.hexashoppingmallservice.service.member.MemberStatusService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = MemberStatusController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MemberStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberStatusService memberStatusService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void getMemberStatus() throws Exception {
        List<MemberStatus> statuses = List.of(
                new MemberStatus("Active"),
                new MemberStatus("Inactive")
        );
        statuses.get(0).setStatusId(1L);
        statuses.get(1).setStatusId(2L);
        given(memberStatusService.getAllMemberStatus()).willReturn(statuses);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/memberStatus")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("get-member-status", preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].statusId").description("상태 ID"),
                                fieldWithPath("[].statusName").description("상태 이름")
                        )));
    }

    @Test
    void createMemberStatus() throws Exception {
        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(1L);
        given(memberStatusService.createMemberStatus(any(MemberStatus.class))).willReturn(memberStatus);

        mockMvc.perform(post("/api/memberStatus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberStatus)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusName").value("Active"))
                .andDo(document("create-member-status", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("statusId").description("상태 ID").optional(),
                                fieldWithPath("statusName").description("상태 이름")
                        ),
                        responseFields(
                                fieldWithPath("statusId").description("상태 ID"),
                                fieldWithPath("statusName").description("상태 이름")
                        )));
    }

    @Test
    void deleteMemberStatus() throws Exception {
        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(1L);

        given(memberStatusService.getMemberStatus(1L)).willReturn(memberStatus);
        doNothing().when(memberStatusService).deleteMemberStatus(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/memberStatus/{memberStatusId}", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("delete-member-status",
                        pathParameters(
                                parameterWithName("memberStatusId").description("삭제할 상태 ID")
                        )));
    }

    @Test
    void deleteMemberStatus_notFound() throws Exception {
        given(memberStatusService.getMemberStatus(1L)).willReturn(null);
        doThrow(new MemberStatusNotFoundException("1")).when(memberStatusService).deleteMemberStatus(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/memberStatus/{memberStatusId}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteMemberStatus_sqlQueryExecuteFail() throws Exception {
        MemberStatus memberStatus = new MemberStatus("Active");
        memberStatus.setStatusId(1L);

        given(memberStatusService.getMemberStatus(1L)).willReturn(memberStatus);
        doThrow(new SqlQueryExecuteFailException("Failed to delete member status"))
                .when(memberStatusService).deleteMemberStatus(1L);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/memberStatus/{memberStatusId}", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateMemberStatus() throws Exception {
        MemberStatusRequestDTO requestDTO = new MemberStatusRequestDTO("Inactive");
        MemberStatus updatedStatus = new MemberStatus("Inactive");

        updatedStatus.setStatusId(1L);

        given(memberStatusService.updateMemberStatus(eq(1L), any(MemberStatusRequestDTO.class))).willReturn(updatedStatus);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/memberStatus/{memberStatusId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusName").value("Inactive"))
                .andDo(document("update-member-status", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberStatusId").description("수정할 상태 ID")
                        ),
                        requestFields(
                                fieldWithPath("statusName").description("수정할 상태 이름")
                        ),
                        responseFields(
                                fieldWithPath("statusId").description("상태 ID").optional(),
                                fieldWithPath("statusName").description("상태 이름")
                        )));
    }
}

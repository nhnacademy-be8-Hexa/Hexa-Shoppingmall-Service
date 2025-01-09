package com.nhnacademy.hexashoppingmallservice.controller.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.address.AddressRequestDTO;
import com.nhnacademy.hexashoppingmallservice.projection.address.AddressProjection;
import com.nhnacademy.hexashoppingmallservice.service.address.AddressService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static co.elastic.clients.elasticsearch.ingest.Processor.Kind.Sort;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private final String BASE_URL = "/api/members/{memberId}/addresses";
    private final String MEMBER_ID = "member123";

    /**
     * 새로운 주소를 추가하는 테스트
     */
    @Test
    void testAddAddress() throws Exception {
        // Arrange
        AddressRequestDTO addressRequest = new AddressRequestDTO();
        addressRequest.setAddressName("집");
        addressRequest.setZoneCode("12345");
        addressRequest.setAddress("서울시 강남구"); // 'address' 필드 추가
        addressRequest.setAddressDetail("서울시 강남구 역삼동 123-45");

        ResultActions result = mockMvc.perform(post(BASE_URL, MEMBER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addressRequest))
                .header("Authorization", "Bearer dummy-token")); // Authorization 헤더 추가

        result.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.addressName").value("집"))
                .andExpect(jsonPath("$.zoneCode").value("12345"))
                .andExpect(jsonPath("$.address").value("서울시 강남구")) // 'address' 필드 검증 추가
                .andExpect(jsonPath("$.addressDetail").value("서울시 강남구 역삼동 123-45"))
                .andDo(document("add-address",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("주소를 추가할 회원의 ID")
                        ),
                        requestFields(
                                fieldWithPath("addressName").description("주소의 이름"),
                                fieldWithPath("zoneCode").description("주소의 우편번호"),
                                fieldWithPath("address").description("주소의 기본 정보"),
                                fieldWithPath("addressDetail").description("상세 주소 정보")
                        ),
                        responseFields(
                                fieldWithPath("addressName").description("주소의 이름"),
                                fieldWithPath("zoneCode").description("주소의 우편번호"),
                                fieldWithPath("address").description("주소의 기본 정보"),
                                fieldWithPath("addressDetail").description("상세 주소 정보")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증을 위한 JWT 토큰")
                        )
                ));
    }
    @Test
    void testGetAddresses() throws Exception {
        AddressProjection address1 = new AddressProjection() {
            @Override
            public String getAddressId() { return "addr1"; }
            @Override
            public String getAddressName() { return "집"; }
            @Override
            public String getZoneCode() { return "12345"; }
            @Override
            public String getAddress() { return "서울시 강남구 역삼동 123-45"; }
            @Override
            public String getAddressDetail() { return "서울시 강남구 역삼동 123-45"; }
        };

        AddressProjection address2 = new AddressProjection() {
            @Override
            public String getAddressId() { return "addr2"; }
            @Override
            public String getAddressName() { return "회사"; }
            @Override
            public String getZoneCode() { return "67890"; }
            @Override
            public String getAddress() { return "서울시 서초구 서초동 678-90"; }
            @Override
            public String getAddressDetail() { return "서울시 서초구 서초동 678-90"; }
        };

        List<AddressProjection> addressList = Arrays.asList(address1, address2);

        Pageable pageable = PageRequest.of(0, 10);
        when(addressService.getAddress(eq(pageable), eq(MEMBER_ID))).thenReturn(addressList);

        // Act
        ResultActions result = mockMvc.perform(get(BASE_URL, MEMBER_ID)
                .param("page", "0")
                .param("size", "10")
                .param("sort", "")
                .header("Authorization", "Bearer dummy-token"));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].addressId").value("addr1"))
                .andExpect(jsonPath("$[0].addressName").value("집"))
                .andExpect(jsonPath("$[0].zoneCode").value("12345"))
                .andExpect(jsonPath("$[0].address").value("서울시 강남구 역삼동 123-45"))
                .andExpect(jsonPath("$[0].addressDetail").value("서울시 강남구 역삼동 123-45"))
                .andExpect(jsonPath("$[1].addressId").value("addr2"))
                .andExpect(jsonPath("$[1].addressName").value("회사"))
                .andExpect(jsonPath("$[1].zoneCode").value("67890"))
                .andExpect(jsonPath("$[1].address").value("서울시 서초구 서초동 678-90"))
                .andExpect(jsonPath("$[1].addressDetail").value("서울시 서초구 서초동 678-90"))
                .andDo(document("get-addresses",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("주소를 조회할 회원의 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 크기"),
                                parameterWithName("sort").description("정렬 기준 (예: property, direction)")
                        ),
                        responseFields(
                                fieldWithPath("[].addressId").description("주소의 ID"),
                                fieldWithPath("[].addressName").description("주소의 이름"),
                                fieldWithPath("[].zoneCode").description("주소의 우편번호"),
                                fieldWithPath("[].address").description("도로명주소"),
                                fieldWithPath("[].addressDetail").description("상세 주소 정보")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증을 위한 JWT 토큰")
                        )
                ));
    }

    /**
     * 특정 주소를 삭제하는 테스트
     */
    @Test
    void testDeleteAddress() throws Exception {
        // Arrange
        Long addressId = 1L;

        // JwtUtils가 사용자 접근을 보장하도록 모킹
//        doNothing().when(jwtUtils).ensureUserAccess(HttpServletRequest.class), eq(MEMBER_ID));

        // AddressService가 주소를 삭제하도록 모킹
        doNothing().when(addressService).deleteAddress(eq(addressId));

        // Act
        ResultActions result = mockMvc.perform(delete("/api/members/{memberId}/addresses/{addressId}", MEMBER_ID, addressId)
                .header("Authorization", "Bearer dummy-token"));

        // Assert
        result.andExpect(status().isNoContent())
                .andDo(document("delete-address",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("회원의 ID"),
                                parameterWithName("addressId").description("삭제할 주소의 ID")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("인증을 위한 JWT 토큰")
                        )
                ));
    }
}
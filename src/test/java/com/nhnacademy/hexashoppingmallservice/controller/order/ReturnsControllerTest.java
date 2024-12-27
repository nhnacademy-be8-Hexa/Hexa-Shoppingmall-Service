package com.nhnacademy.hexashoppingmallservice.controller.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.hexashoppingmallservice.dto.order.ReturnsRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.*;
import com.nhnacademy.hexashoppingmallservice.service.order.ReturnsService;
import com.nhnacademy.hexashoppingmallservice.util.JwtUtils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = ReturnsController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class ReturnsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReturnsService returnsService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());


    private MemberStatus memberStatus;
    private Rating rating;
    private Member member;
    private WrappingPaper wrappingPaper;
    private OrderStatus orderStatus;
    private Order order;
    private Returns returns;
    private ReturnsReason returnsReason;

    @BeforeEach
    void setUp() throws Exception{

        // 멤버 관련
        rating = Rating.of("Gold", 90);

        Field ratingIdField = rating.getClass().getDeclaredField("ratingId");
        ratingIdField.setAccessible(true);
        ratingIdField.set(rating, 1L);

        memberStatus = MemberStatus.of("Active");

        Field memberStatusIdField = memberStatus.getClass().getDeclaredField("statusId");
        memberStatusIdField.setAccessible(true);
        memberStatusIdField.set(memberStatus, 1L);

        member = createMember(rating, memberStatus);

        Field memberLastLoginAtField = member.getClass().getDeclaredField("memberLastLoginAt");
        memberLastLoginAtField.setAccessible(true);
        memberLastLoginAtField.set(member, LocalDateTime.now());


        // 주문관련
        wrappingPaper = WrappingPaper.builder().wrappingPaperName("Basic").wrappingPaperPrice(1000).build();

        Field wrappingPaperIdField = wrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        wrappingPaperIdField.setAccessible(true);
        wrappingPaperIdField.set(wrappingPaper, 1L);

        orderStatus = OrderStatus.builder().orderStatus("DELIVERED").build();

        Field orderStatusIdField = orderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusIdField.setAccessible(true);
        orderStatusIdField.set(orderStatus, 1L);


        order = createOrder(member, wrappingPaper, orderStatus, LocalDateTime.now());

        Field orderIdField = order.getClass().getDeclaredField("orderId");
        orderIdField.setAccessible(true);
        orderIdField.set(order, 1L);


        returnsReason = ReturnsReason.of("test reason");

        Field returnReasonIdField = returnsReason.getClass().getDeclaredField("returnsReasonId");
        returnReasonIdField.setAccessible(true);
        returnReasonIdField.set(returnsReason, 1L);


        returns = Returns.of(order, returnsReason , "Detailed return reason");

        Field returnsOrderIdField = returns.getClass().getDeclaredField("orderId");
        returnsOrderIdField.setAccessible(true);
        returnsOrderIdField.set(returns, 1L);
    }

    @Test
    void createReturns() throws Exception {
        ReturnsRequestDTO request = new ReturnsRequestDTO(1L,1L, "Detailed explanation for return");
        
        given(returnsService.createReturns(any(ReturnsRequestDTO.class))).willReturn(returns);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/returns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReason.returnsReason").value("test reason"))
                .andExpect(jsonPath("$.returnsDetail").value("Detailed return reason"))
                .andDo(document("create-returns",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("order.orderId").description("주문의 ID"),
                                fieldWithPath("order.member.memberId").description("회원 ID"),
                                fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("order.member.memberName").description("회원 이름"),
                                fieldWithPath("order.member.memberNumber").description("회원 연락처"),
                                fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("order.member.memberCreatedAt").description("회원 가입 날짜"),
                                fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 날짜"),
                                fieldWithPath("order.member.memberRole").description("회원 역할"),
                                fieldWithPath("order.member.rating.ratingId").description("등급 ID"),
                                fieldWithPath("order.member.rating.ratingName").description("등급 이름"),
                                fieldWithPath("order.member.rating.ratingPercent").description("등급 퍼센트"),
                                fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),
                                fieldWithPath("order.orderPrice").description("주문 가격"),
                                fieldWithPath("order.orderedAt").description("주문 시간"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장지 ID"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장지 이름"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장지 가격"),
                                fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                fieldWithPath("order.orderStatus.orderStatus").description("주문 상태"),
                                fieldWithPath("order.zoneCode").description("우편번호"),
                                fieldWithPath("order.address").description("주소"),
                                fieldWithPath("order.addressDetail").description("상세 주소"),
                                fieldWithPath("returnsReason.returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsReason.returnsReason").description("반품 사유"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        )
                ));
    }

    @Test
    void getAllReturns() throws Exception {

        List<Returns> returnsList = Collections.singletonList(returns);
        given(returnsService.getReturns(any(Pageable.class))).willReturn(returnsList);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/returns")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-all-returns",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                fieldWithPath("[].orderId").description("주문 ID"),
                                fieldWithPath("[].order.orderId").description("주문 ID (주문 객체 내부)"),
                                fieldWithPath("[].order.member.memberId").description("회원 ID"),
                                fieldWithPath("[].order.member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("[].order.member.memberName").description("회원 이름"),
                                fieldWithPath("[].order.member.memberNumber").description("회원 전화번호"),
                                fieldWithPath("[].order.member.memberEmail").description("회원 이메일"),
                                fieldWithPath("[].order.member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("[].order.member.memberCreatedAt").description("회원 생성 날짜"),
                                fieldWithPath("[].order.member.memberLastLoginAt").description("회원 마지막 로그인 날짜"),
                                fieldWithPath("[].order.member.memberRole").description("회원 역할"),
                                fieldWithPath("[].order.member.rating.ratingId").description("회원 등급 ID"),
                                fieldWithPath("[].order.member.rating.ratingName").description("회원 등급 이름"),
                                fieldWithPath("[].order.member.rating.ratingPercent").description("회원 등급 할인율"),
                                fieldWithPath("[].order.member.memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("[].order.member.memberStatus.statusName").description("회원 상태 이름"),
                                fieldWithPath("[].order.orderPrice").description("주문 가격"),
                                fieldWithPath("[].order.orderedAt").description("주문 날짜"),
                                fieldWithPath("[].order.wrappingPaper.wrappingPaperId").description("포장지 ID"),
                                fieldWithPath("[].order.wrappingPaper.wrappingPaperName").description("포장지 이름"),
                                fieldWithPath("[].order.wrappingPaper.wrappingPaperPrice").description("포장지 가격"),
                                fieldWithPath("[].order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                fieldWithPath("[].order.orderStatus.orderStatus").description("주문 상태"),
                                fieldWithPath("[].order.zoneCode").description("우편번호"),
                                fieldWithPath("[].order.address").description("주소"),
                                fieldWithPath("[].order.addressDetail").description("상세 주소"),
                                fieldWithPath("[].returnsReason.returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("[].returnsReason.returnsReason").description("반품 사유"),
                                fieldWithPath("[].returnsDetail").description("반품 상세 사유")

                        )
                ));
    }

    @Test
    void getReturnsByReturnsReasonId() throws Exception {
        given(returnsService.getReturnsByReturnsReasonId(anyLong())).willReturn(returns);
        returns.setReturnsReason(returnsReason);
        given(returnsService.getReturnsByReturnsReasonId(anyLong())).willReturn(returns);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/returns/returnReason/{ReturnReasonId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReason.returnsReason").value("test reason"))
                .andExpect(jsonPath("$.returnsDetail").value("Detailed return reason"))
                .andDo(document("get-returns-by-reason-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ReturnReasonId").description("반품 사유 ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("order.orderId").description("주문의 ID"),
                                fieldWithPath("order.member.memberId").description("회원 ID"),
                                fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("order.member.memberName").description("회원 이름"),
                                fieldWithPath("order.member.memberNumber").description("회원 연락처"),
                                fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("order.member.memberCreatedAt").description("회원 가입 날짜"),
                                fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 날짜"),
                                fieldWithPath("order.member.memberRole").description("회원 역할"),
                                fieldWithPath("order.member.rating.ratingId").description("등급 ID"),
                                fieldWithPath("order.member.rating.ratingName").description("등급 이름"),
                                fieldWithPath("order.member.rating.ratingPercent").description("회원 등급 할인률"),
                                fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),
                                fieldWithPath("order.orderPrice").description("주문 가격"),
                                fieldWithPath("order.orderedAt").description("주문 시간"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장지 ID"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장지 이름"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장지 가격"),
                                fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                fieldWithPath("order.orderStatus.orderStatus").description("주문 상태"),
                                fieldWithPath("order.zoneCode").description("우편번호"),
                                fieldWithPath("order.address").description("주소"),
                                fieldWithPath("order.addressDetail").description("상세 주소"),
                                fieldWithPath("returnsReason.returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsReason.returnsReason").description("반품 사유"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        )
                ));
    }

    @Test
    void deleteReturns() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/returns/order/{orderId}", 1L))
                .andExpect(status().isNoContent())
                .andDo(document("delete-returns",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        )
                ));
    }

    @Test
    void getReturnsByOrderId() throws Exception {
        given(returnsService.getReturnsByOrderId(anyLong())).willReturn(returns);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/returns/order/{orderId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReason.returnsReason").value("test reason"))
                .andExpect(jsonPath("$.returnsDetail").value("Detailed return reason"))
                .andDo(document("get-returns-by-order-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("order.orderId").description("주문의 ID"),
                                fieldWithPath("order.member.memberId").description("회원 ID"),
                                fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("order.member.memberName").description("회원 이름"),
                                fieldWithPath("order.member.memberNumber").description("회원 연락처"),
                                fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("order.member.memberCreatedAt").description("회원 가입 날짜"),
                                fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 날짜"),
                                fieldWithPath("order.member.memberRole").description("회원 역할"),
                                fieldWithPath("order.member.rating.ratingId").description("등급 ID"),
                                fieldWithPath("order.member.rating.ratingName").description("등급 이름"),
                                fieldWithPath("order.member.rating.ratingPercent").description("회원 등급 할인률"),
                                fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),
                                fieldWithPath("order.orderPrice").description("주문 가격"),
                                fieldWithPath("order.orderedAt").description("주문 시간"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장지 ID"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장지 이름"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장지 가격"),
                                fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                fieldWithPath("order.orderStatus.orderStatus").description("주문 상태"),
                                fieldWithPath("order.zoneCode").description("우편번호"),
                                fieldWithPath("order.address").description("주소"),
                                fieldWithPath("order.addressDetail").description("상세 주소"),
                                fieldWithPath("returnsReason.returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsReason.returnsReason").description("반품 사유"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        )
                ));
    }


    private Member createMember(Rating rating, MemberStatus memberStatus) {
        return Member.of(
                "member123", "password123", "홍길동", "01012345678",
                "test@naver.com", LocalDate.of(1990, 1, 1), rating, memberStatus
        );
    }

    private Order createOrder(Member member, WrappingPaper wrappingPaper, OrderStatus orderStatus, LocalDateTime orderedAt) {
        return Order.builder()
                .member(member)
                .orderPrice(20000)
                .orderedAt(orderedAt)
                .wrappingPaper(wrappingPaper)
                .orderStatus(orderStatus)
                .zoneCode("10010")
                .address("서울특별시 강남구 역삼동")
                .addressDetail("101동 101호")
                .build();
    }

    @Test
    void getReturnsByMemberId() throws Exception {
        given(returnsService.getReturnsByMemberId(any())).willReturn(returns);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/returns/member/{memberId}", "member123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReason.returnsReason").value("test reason"))
                .andExpect(jsonPath("$.returnsDetail").value("Detailed return reason"))
                .andDo(document("get-returns-by-member-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("memberId").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("order.orderId").description("주문의 ID"),
                                fieldWithPath("order.member.memberId").description("회원 ID"),
                                fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("order.member.memberName").description("회원 이름"),
                                fieldWithPath("order.member.memberNumber").description("회원 연락처"),
                                fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("order.member.memberCreatedAt").description("회원 가입 날짜"),
                                fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 날짜"),
                                fieldWithPath("order.member.memberRole").description("회원 역할"),
                                fieldWithPath("order.member.rating.ratingId").description("등급 ID"),
                                fieldWithPath("order.member.rating.ratingName").description("등급 이름"),
                                fieldWithPath("order.member.rating.ratingPercent").description("회원 등급 할인률"),
                                fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),
                                fieldWithPath("order.orderPrice").description("주문 가격"),
                                fieldWithPath("order.orderedAt").description("주문 시간"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장지 ID"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장지 이름"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장지 가격"),
                                fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                fieldWithPath("order.orderStatus.orderStatus").description("주문 상태"),
                                fieldWithPath("order.zoneCode").description("우편번호"),
                                fieldWithPath("order.address").description("주소"),
                                fieldWithPath("order.addressDetail").description("상세 주소"),
                                fieldWithPath("returnsReason.returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsReason.returnsReason").description("반품 사유"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        )
                ));
    }

    @Test
    void updateReturns() throws Exception {
        ReturnsRequestDTO request = new ReturnsRequestDTO(1L, 1L, "Updated return reason");
        given(returnsService.updateReturns(anyLong(), any(ReturnsRequestDTO.class))).willReturn(returns);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/returns/order/{orderId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnsReason.returnsReason").value("test reason"))
                .andExpect(jsonPath("$.returnsDetail").value("Detailed return reason"))
                .andDo(document("update-returns",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("orderId").description("주문 ID")
                        ),
                        requestFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        ),
                        responseFields(
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("order.orderId").description("주문의 ID"),
                                fieldWithPath("order.member.memberId").description("회원 ID"),
                                fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                fieldWithPath("order.member.memberName").description("회원 이름"),
                                fieldWithPath("order.member.memberNumber").description("회원 연락처"),
                                fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                fieldWithPath("order.member.memberCreatedAt").description("회원 가입 날짜"),
                                fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 날짜"),
                                fieldWithPath("order.member.memberRole").description("회원 역할"),
                                fieldWithPath("order.member.rating.ratingId").description("등급 ID"),
                                fieldWithPath("order.member.rating.ratingName").description("등급 이름"),
                                fieldWithPath("order.member.rating.ratingPercent").description("회원 등급 할인률"),
                                fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),
                                fieldWithPath("order.orderPrice").description("주문 가격"),
                                fieldWithPath("order.orderedAt").description("주문 시간"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장지 ID"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장지 이름"),
                                fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장지 가격"),
                                fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                fieldWithPath("order.orderStatus.orderStatus").description("주문 상태"),
                                fieldWithPath("order.zoneCode").description("우편번호"),
                                fieldWithPath("order.address").description("주소"),
                                fieldWithPath("order.addressDetail").description("상세 주소"),
                                fieldWithPath("returnsReason.returnsReasonId").description("반품 사유 ID"),
                                fieldWithPath("returnsReason.returnsReason").description("반품 사유"),
                                fieldWithPath("returnsDetail").description("반품 상세 사유")
                        )
                ));
    }
}
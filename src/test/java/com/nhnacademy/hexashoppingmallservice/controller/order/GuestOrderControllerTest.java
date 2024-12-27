package com.nhnacademy.hexashoppingmallservice.controller.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.hexashoppingmallservice.dto.order.GuestOrderRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.entity.order.GuestOrder;
import com.nhnacademy.hexashoppingmallservice.entity.order.Order;
import com.nhnacademy.hexashoppingmallservice.entity.order.OrderStatus;
import com.nhnacademy.hexashoppingmallservice.entity.order.WrappingPaper;
import com.nhnacademy.hexashoppingmallservice.projection.order.GuestOrderProjection;
import com.nhnacademy.hexashoppingmallservice.service.order.GuestOrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(controllers = GuestOrderController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class GuestOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestOrderService guestOrderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test createGuestOrder - Success")
    void testCreateGuestOrder_Success() throws Exception {

        WrappingPaper wrappingPaper = WrappingPaper.builder().wrappingPaperName("Basic").wrappingPaperPrice(1000).build();
        Field warderPaperField = wrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        warderPaperField.setAccessible(true);
        warderPaperField.set(wrappingPaper, 1L);


        OrderStatus orderStatus = OrderStatus.builder().orderStatus("DELIVERED").build();
        Field orderStatusField = orderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusField.setAccessible(true);
        orderStatusField.set(orderStatus, 1L);

        Rating rating = Rating.of("Gold", 90);
        Field ratingField = rating.getClass().getDeclaredField("ratingId");
        ratingField.setAccessible(true);
        ratingField.set(rating, 1L);

        MemberStatus memberStatus = MemberStatus.of("Active");
        Field memberStatusField = memberStatus.getClass().getDeclaredField("statusId");
        memberStatusField.setAccessible(true);
        memberStatusField.set(memberStatus, 1L);

        Member member = Member.of(
                "member123", "password123", "홍길동", "01012345678",
                "test@naver.com", LocalDate.of(1990, 1, 1), rating, memberStatus
        );
        Field memberLastLoginField = member.getClass().getDeclaredField("memberLastLoginAt");
        memberLastLoginField.setAccessible(true);
        memberLastLoginField.set(member, LocalDateTime.now());

        Order order = Order.builder()
                .member(member)
                .orderPrice(20000)
                .orderedAt(LocalDateTime.now())
                .wrappingPaper(wrappingPaper)
                .orderStatus(orderStatus)
                .zoneCode("10010")
                .address("서울특별시 강남구 역삼동")
                .addressDetail("101동 101호")
                .build();

        Field orderField = order.getClass().getDeclaredField("orderId");
        orderField.setAccessible(true);
        orderField.set(order, 1L);

        GuestOrderRequestDTO request = new GuestOrderRequestDTO(
                1L,
                "securePassword",
                "12345678901",
                "email@example.com"
        );

        GuestOrder response = GuestOrder.builder()
                .order(order)
                .guestOrderPassword(request.getGuestOrderPassword())
                .guestOrderNumber(request.getGuestOrderNumber())
                .guestOrderEmail(request.getGuestOrderEmail())
                .build();

        Field responseOrderId = response.getClass().getDeclaredField("orderId");
        responseOrderId.setAccessible(true);
        responseOrderId.set(response, request.getOrderId());

        Mockito.when(guestOrderService.createGuestOrder(any(GuestOrderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/guestOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(document("create-guest-order",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderPassword").description("게스트 주문 비밀번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("게스트 주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderPassword").description("게스트 주문 비밀번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("게스트 주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일"),

                                // `order` 객체에 포함된 필드들
                                PayloadDocumentation.fieldWithPath("order").description("주문 정보"),
                                PayloadDocumentation.fieldWithPath("order.orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("order.orderPrice").description("주문 가격"),
                                PayloadDocumentation.fieldWithPath("order.orderedAt").description("주문 일자"),
                                PayloadDocumentation.fieldWithPath("order.zoneCode").description("지역 코드"),
                                PayloadDocumentation.fieldWithPath("order.address").description("주소"),
                                PayloadDocumentation.fieldWithPath("order.addressDetail").description("주소 상세 정보"),

                                // `member` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.member").description("회원 정보"),
                                PayloadDocumentation.fieldWithPath("order.member.memberId").description("회원 ID"),
                                PayloadDocumentation.fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                PayloadDocumentation.fieldWithPath("order.member.memberName").description("회원 이름"),
                                PayloadDocumentation.fieldWithPath("order.member.memberNumber").description("회원 전화번호"),
                                PayloadDocumentation.fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                PayloadDocumentation.fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                PayloadDocumentation.fieldWithPath("order.member.memberCreatedAt").description("회원 가입 일자"),
                                PayloadDocumentation.fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 일자"),
                                PayloadDocumentation.fieldWithPath("order.member.memberRole").description("회원 역할"),

                                // `rating` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.member.rating").description("회원 등급"),
                                PayloadDocumentation.fieldWithPath("order.member.rating.ratingId").description("회원 등급 ID"),
                                PayloadDocumentation.fieldWithPath("order.member.rating.ratingName").description("회원 등급 이름"),
                                PayloadDocumentation.fieldWithPath("order.member.rating.ratingPercent").description("회원 등급 비율"),

                                // `memberStatus` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.member.memberStatus").description("회원 상태"),
                                PayloadDocumentation.fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                PayloadDocumentation.fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),

                                // `wrappingPaper` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper").description("포장 정보"),
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장 ID"),
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장 이름"),
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장 가격"),

                                // `orderStatus` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.orderStatus").description("주문 상태"),
                                PayloadDocumentation.fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                PayloadDocumentation.fieldWithPath("order.orderStatus.orderStatus").description("주문 상태 이름")
                        )
                ));
    }

    @Test
    @DisplayName("Test createGuestOrder - Validation Error")
    void testCreateGuestOrder_ValidationError() throws Exception {
        GuestOrderRequestDTO request = new GuestOrderRequestDTO(
                (Long) null,
                "",
                "123",
                "invalid-email"
        );

        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/guestOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(document("create-guest-order-validation-error",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderPassword").description("게스트 주문 비밀번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("게스트 주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("Test getAllGuestOrders - Success")
    void testGetAllGuestOrders_Success() throws Exception {
        List<GuestOrderProjection> projections = List.of(
                new GuestOrderProjection() {
                    @Override
                    public Long getOrderId() {
                        return 1L;
                    }

                    @Override
                    public String getGuestOrderNumber() {
                        return "12345678901";
                    }

                    @Override
                    public String getGuestOrderEmail() {
                        return "email1@example.com";
                    }
                },
                new GuestOrderProjection() {
                    @Override
                    public Long getOrderId() {
                        return 2L;
                    }

                    @Override
                    public String getGuestOrderNumber() {
                        return "98765432101";
                    }

                    @Override
                    public String getGuestOrderEmail() {
                        return "email2@example.com";
                    }
                }
        );

        Mockito.when(guestOrderService.getGuestOrders(any(Pageable.class)))
                .thenReturn(projections);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/guestOrders?page=0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].guestOrderNumber").value("12345678901"))
                .andExpect(jsonPath("$[0].guestOrderEmail").value("email1@example.com"))
                .andExpect(jsonPath("$[1].orderId").value(2L))
                .andExpect(jsonPath("$[1].guestOrderNumber").value("98765432101"))
                .andExpect(jsonPath("$[1].guestOrderEmail").value("email2@example.com"))
                .andDo(document("get-all-guest-orders",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                RequestDocumentation.parameterWithName("page").description("페이지 번호")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("[].orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("[].guestOrderNumber").description("주문 번호"),
                                PayloadDocumentation.fieldWithPath("[].guestOrderEmail").description("게스트 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("Test getGuestOrder - Success")
    void testGetGuestOrder_Success() throws Exception {
        GuestOrderProjection projection = new GuestOrderProjection() {
            @Override
            public Long getOrderId() {
                return 1L;
            }

            @Override
            public String getGuestOrderNumber() {
                return "12345678901";
            }

            @Override
            public String getGuestOrderEmail() {
                return "email@example.com";
            }
        };

        Mockito.when(guestOrderService.getGuestOrder(1L)).thenReturn(projection);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/guestOrders/{orderId}",1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.guestOrderNumber").value("12345678901"))
                .andExpect(jsonPath("$.guestOrderEmail").value("email@example.com"))
                .andDo(document("get-guest-order",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                RequestDocumentation.parameterWithName("orderId").description("주문 ID")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("Test updateGuestOrder - Success")
    void testUpdateGuestOrder_Success() throws Exception {


        WrappingPaper wrappingPaper = WrappingPaper.builder().wrappingPaperName("Basic").wrappingPaperPrice(1000).build();
        Field warderPaperField = wrappingPaper.getClass().getDeclaredField("wrappingPaperId");
        warderPaperField.setAccessible(true);
        warderPaperField.set(wrappingPaper, 1L);


        OrderStatus orderStatus = OrderStatus.builder().orderStatus("DELIVERED").build();
        Field orderStatusField = orderStatus.getClass().getDeclaredField("orderStatusId");
        orderStatusField.setAccessible(true);
        orderStatusField.set(orderStatus, 1L);

        Rating rating = Rating.of("Gold", 90);
        Field ratingField = rating.getClass().getDeclaredField("ratingId");
        ratingField.setAccessible(true);
        ratingField.set(rating, 1L);

        MemberStatus memberStatus = MemberStatus.of("Active");
        Field memberStatusField = memberStatus.getClass().getDeclaredField("statusId");
        memberStatusField.setAccessible(true);
        memberStatusField.set(memberStatus, 1L);

        Member member = Member.of(
                "member123", "password123", "홍길동", "01012345678",
                "test@naver.com", LocalDate.of(1990, 1, 1), rating, memberStatus
        );
        Field memberLastLoginField = member.getClass().getDeclaredField("memberLastLoginAt");
        memberLastLoginField.setAccessible(true);
        memberLastLoginField.set(member, LocalDateTime.now());

        Order order = Order.builder()
                .member(member)
                .orderPrice(20000)
                .orderedAt(LocalDateTime.now())
                .wrappingPaper(wrappingPaper)
                .orderStatus(orderStatus)
                .zoneCode("10010")
                .address("서울특별시 강남구 역삼동")
                .addressDetail("101동 101호")
                .build();

        Field orderField = order.getClass().getDeclaredField("orderId");
        orderField.setAccessible(true);
        orderField.set(order, 1L);


        GuestOrderRequestDTO request = new GuestOrderRequestDTO(
                1L,
                "updatedSecurePassword",
                "09876543210",
                "updatedEmail@example.com"
        );

        GuestOrder response = GuestOrder.builder()
                .order(order)
                .guestOrderPassword(request.getGuestOrderPassword())
                .guestOrderNumber(request.getGuestOrderNumber())
                .guestOrderEmail(request.getGuestOrderEmail())
                .build();


        Field responseOrderId = response.getClass().getDeclaredField("orderId");
        responseOrderId.setAccessible(true);
        responseOrderId.set(response, request.getOrderId());


        Mockito.when(guestOrderService.updateGuestOrder(any(GuestOrderRequestDTO.class)))
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/guestOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(response)))
                .andDo(document("update-guest-order",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderPassword").description("게스트 주문 비밀번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("게스트 주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일")
                        ),
                        responseFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderPassword").description("게스트 주문 비밀번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("게스트 주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일"),

                                // `order` 객체에 포함된 필드들
                                PayloadDocumentation.fieldWithPath("order").description("주문 정보"),
                                PayloadDocumentation.fieldWithPath("order.orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("order.orderPrice").description("주문 가격"),
                                PayloadDocumentation.fieldWithPath("order.orderedAt").description("주문 일자"),
                                PayloadDocumentation.fieldWithPath("order.zoneCode").description("지역 코드"),
                                PayloadDocumentation.fieldWithPath("order.address").description("주소"),
                                PayloadDocumentation.fieldWithPath("order.addressDetail").description("주소 상세 정보"),

                                // `member` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.member").description("회원 정보"),
                                PayloadDocumentation.fieldWithPath("order.member.memberId").description("회원 ID"),
                                PayloadDocumentation.fieldWithPath("order.member.memberPassword").description("회원 비밀번호"),
                                PayloadDocumentation.fieldWithPath("order.member.memberName").description("회원 이름"),
                                PayloadDocumentation.fieldWithPath("order.member.memberNumber").description("회원 전화번호"),
                                PayloadDocumentation.fieldWithPath("order.member.memberEmail").description("회원 이메일"),
                                PayloadDocumentation.fieldWithPath("order.member.memberBirthAt").description("회원 생년월일"),
                                PayloadDocumentation.fieldWithPath("order.member.memberCreatedAt").description("회원 가입 일자"),
                                PayloadDocumentation.fieldWithPath("order.member.memberLastLoginAt").description("회원 마지막 로그인 일자"),
                                PayloadDocumentation.fieldWithPath("order.member.memberRole").description("회원 역할"),

                                // `rating` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.member.rating").description("회원 등급"),
                                PayloadDocumentation.fieldWithPath("order.member.rating.ratingId").description("회원 등급 ID"),
                                PayloadDocumentation.fieldWithPath("order.member.rating.ratingName").description("회원 등급 이름"),
                                PayloadDocumentation.fieldWithPath("order.member.rating.ratingPercent").description("회원 등급 비율"),

                                // `memberStatus` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.member.memberStatus").description("회원 상태"),
                                PayloadDocumentation.fieldWithPath("order.member.memberStatus.statusId").description("회원 상태 ID"),
                                PayloadDocumentation.fieldWithPath("order.member.memberStatus.statusName").description("회원 상태 이름"),

                                // `wrappingPaper` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper").description("포장 정보"),
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper.wrappingPaperId").description("포장 ID"),
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper.wrappingPaperName").description("포장 이름"),
                                PayloadDocumentation.fieldWithPath("order.wrappingPaper.wrappingPaperPrice").description("포장 가격"),

                                // `orderStatus` 객체 필드
                                PayloadDocumentation.fieldWithPath("order.orderStatus").description("주문 상태"),
                                PayloadDocumentation.fieldWithPath("order.orderStatus.orderStatusId").description("주문 상태 ID"),
                                PayloadDocumentation.fieldWithPath("order.orderStatus.orderStatus").description("주문 상태 이름")
                        )
                ));
    }


    @Test
    @DisplayName("Test updateGuestOrder - Validation Error")
    void testUpdateGuestOrder_ValidationError() throws Exception {
        // 잘못된 데이터로 요청을 구성
        GuestOrderRequestDTO request = new GuestOrderRequestDTO(
                1L,
                "jflksdjfskadljkldsjfslakfjaslkvjakldfjdslakfjsdlkfsjaflkdjfkldsjflksajvlkdvjslkdfjdklsfjdlkfjdlksjfkll",
                "123",
                "invalid-email"
        );

        mockMvc.perform(put("/api/guestOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(document("update-guest-order-validation-error",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                PayloadDocumentation.fieldWithPath("orderId").description("주문 ID"),
                                PayloadDocumentation.fieldWithPath("guestOrderPassword").description("게스트 주문 비밀번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderNumber").description("게스트 주문 번호"),
                                PayloadDocumentation.fieldWithPath("guestOrderEmail").description("게스트 이메일")
                        )
                ));
    }
}
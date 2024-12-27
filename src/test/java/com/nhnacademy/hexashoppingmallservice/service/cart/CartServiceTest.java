package com.nhnacademy.hexashoppingmallservice.service.cart;

import com.nhnacademy.hexashoppingmallservice.dto.cart.CartRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.book.BookStatus;
import com.nhnacademy.hexashoppingmallservice.entity.book.Publisher;
import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.entity.member.MemberStatus;
import com.nhnacademy.hexashoppingmallservice.entity.member.Rating;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotExistException;
import com.nhnacademy.hexashoppingmallservice.exception.cartException.CartNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.projection.cart.CartProjection;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.cart.CartRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

    private Member member;
    private Book book;
    private Publisher publisher;
    private Rating rating;
    private MemberStatus memberStatus;
    private BookStatus bookStatus;

    @BeforeEach
    void setUp() {
        // Publisher 설정
        publisher = Publisher.builder()
                .publisherId(1L)
                .publisherName("Test Publisher")
                .build();

        // Rating 설정
        rating = Rating.of("Good", 80);

        // MemberStatus 설정
        memberStatus = MemberStatus.of("Active");

        // BookStatus 설정
        bookStatus = BookStatus.of("Available");

        // Member 설정
        member = Member.of(
                "member123",
                "password123",
                "John Doe",
                "01012345678",
                "john.doe@example.com",
                LocalDate.of(1990, 1, 1),
                rating,
                memberStatus
        );

        // Book 설정
        book = Book.of(
                "Test Book",
                "This is a test book description.",
                LocalDate.of(2020, 1, 1),
                1234567890123L,
                10000,
                8000,
                publisher,
                bookStatus
        );
    }

    @Nested
    @DisplayName("createCart 메서드 테스트")
    class CreateCartTests {

        @Test
        @DisplayName("성공적으로 카트를 생성한다")
        void createCart_Success() {
            // Arrange
            CartRequestDTO cartRequestDTO = new CartRequestDTO("member123", book.getBookId(), 2);

            when(memberRepository.findById("member123")).thenReturn(Optional.of(member));
            when(bookRepository.findById(book.getBookId())).thenReturn(Optional.of(book));
            when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
                Cart cart = invocation.getArgument(0);
                cart.setCartId(1L); // 예를 들어 ID를 설정
                return cart;
            });

            // Act
            cartService.createCart(cartRequestDTO);

            // Assert
            ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
            verify(cartRepository, times(1)).save(cartCaptor.capture());

            Cart savedCart = cartCaptor.getValue();
            assertThat(savedCart.getCartId()).isNotNull();
            assertThat(savedCart.getMember()).isEqualTo(member);
            assertThat(savedCart.getBook()).isEqualTo(book);
            assertThat(savedCart.getCartAmount()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 회원으로 카트를 생성하려 할 때 예외가 발생한다")
        void createCart_MemberNotFound() {
            // Arrange
            CartRequestDTO cartRequestDTO = new CartRequestDTO("nonexistent_member", book.getBookId(), 2);

            when(memberRepository.findById("nonexistent_member")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cartService.createCart(cartRequestDTO))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining("Member ID: nonexistent_member not found.");

            verify(bookRepository, never()).findById(anyLong());
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("존재하지 않는 책으로 카트를 생성하려 할 때 예외가 발생한다")
        void createCart_BookNotFound() {
            // Arrange
            Long invalidBookId = 999L;
            CartRequestDTO cartRequestDTO = new CartRequestDTO("member123", invalidBookId, 2);

            when(memberRepository.findById("member123")).thenReturn(Optional.of(member));
            when(bookRepository.findById(invalidBookId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cartService.createCart(cartRequestDTO))
                    .isInstanceOf(BookNotExistException.class)
                    .hasMessageContaining("Book ID: " + invalidBookId + " not found.");

            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("getCart 메서드 테스트")
    class GetCartTests {

        @Test
        @DisplayName("성공적으로 특정 카트를 조회한다")
        void getCart_Success() {
            // Arrange
            Long cartId = 1L;

            CartProjection cartProjection = new CartProjection() {
                @Override
                public Long getCartId() {
                    return cartId;
                }

                @Override
                public MemberProjection getMember() {
                    return () -> member.getMemberId();
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return book.getBookId();
                        }

                        @Override
                        public String getBookTitle() {
                            return book.getBookTitle();
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 2;
                }
            };

            when(cartRepository.findByCartId(cartId)).thenReturn(Optional.of(cartProjection));

            // Act
            CartProjection result = cartService.getCart(cartId);

            // Assert
            assertThat(result.getCartId()).isEqualTo(cartId);
            assertThat(result.getMember().getMemberId()).isEqualTo(member.getMemberId());
            assertThat(result.getBook().getBookId()).isEqualTo(book.getBookId());
            assertThat(result.getBook().getBookTitle()).isEqualTo(book.getBookTitle());
            assertThat(result.getCartAmount()).isEqualTo(2);
        }

        @Test
        @DisplayName("존재하지 않는 카트를 조회하려 할 때 예외가 발생한다")
        void getCart_NotFound() {
            // Arrange
            Long cartId = 999L;

            when(cartRepository.findByCartId(cartId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cartService.getCart(cartId))
                    .isInstanceOf(CartNotFoundException.class)
                    .hasMessageContaining("Cart ID: " + cartId + " not found");
        }
    }

    @Nested
    @DisplayName("getCartByMemberId 메서드 테스트")
    class GetCartByMemberIdTests {

        @Test
        @DisplayName("성공적으로 특정 회원의 모든 카트를 조회한다")
        void getCartByMemberId_Success() {
            // Arrange
            String memberId = member.getMemberId();

            CartProjection cart1 = new CartProjection() {
                @Override
                public Long getCartId() {
                    return 1L;
                }

                @Override
                public MemberProjection getMember() {
                    return () -> memberId;
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return book.getBookId();
                        }

                        @Override
                        public String getBookTitle() {
                            return book.getBookTitle();
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 2;
                }
            };

            CartProjection cart2 = new CartProjection() {
                @Override
                public Long getCartId() {
                    return 2L;
                }

                @Override
                public MemberProjection getMember() {
                    return () -> memberId;
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return book.getBookId();
                        }

                        @Override
                        public String getBookTitle() {
                            return book.getBookTitle();
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 3;
                }
            };

            List<CartProjection> cartProjections = Arrays.asList(cart1, cart2);

            when(memberRepository.existsById(memberId)).thenReturn(true);
            when(cartRepository.findAllByMemberMemberId(memberId)).thenReturn(cartProjections);

            // Act
            List<CartProjection> result = cartService.getCartByMemberId(memberId);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCartId()).isEqualTo(1L);
            assertThat(result.get(0).getMember().getMemberId()).isEqualTo(memberId);
            assertThat(result.get(0).getBook().getBookId()).isEqualTo(book.getBookId());
            assertThat(result.get(0).getBook().getBookTitle()).isEqualTo(book.getBookTitle());
            assertThat(result.get(0).getCartAmount()).isEqualTo(2);

            assertThat(result.get(1).getCartId()).isEqualTo(2L);
            assertThat(result.get(1).getMember().getMemberId()).isEqualTo(memberId);
            assertThat(result.get(1).getBook().getBookId()).isEqualTo(book.getBookId());
            assertThat(result.get(1).getBook().getBookTitle()).isEqualTo(book.getBookTitle());
            assertThat(result.get(1).getCartAmount()).isEqualTo(3);
        }

        @Test
        @DisplayName("존재하지 않는 회원의 카트를 조회하려 할 때 예외가 발생한다")
        void getCartByMemberId_MemberNotFound() {
            // Arrange
            String memberId = "nonexistent_member";

            when(memberRepository.existsById(memberId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> cartService.getCartByMemberId(memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining("Member ID: " + memberId + " not found");

            verify(cartRepository, never()).findAllByMemberMemberId(anyString());
        }
    }

    @Nested
    @DisplayName("deleteCart 메서드 테스트")
    class DeleteCartTests {

        @Test
        @DisplayName("성공적으로 특정 카트를 삭제한다")
        void deleteCart_Success() {
            // Arrange
            Long cartId = 1L;

            doNothing().when(cartRepository).deleteById(cartId);

            // Act
            cartService.deleteCart(cartId);

            // Assert
            verify(cartRepository, times(1)).deleteById(cartId);
        }

        // `deleteById`는 존재하지 않는 ID에 대해서도 예외를 던지지 않으므로 별도의 예외 테스트는 필요하지 않습니다.
    }

    @Nested
    @DisplayName("clearCartByMember 메서드 테스트")
    class ClearCartByMemberTests {

        @Test
        @DisplayName("성공적으로 특정 회원의 모든 카트를 삭제한다")
        void clearCartByMember_Success() {
            // Arrange
            String memberId = member.getMemberId();

            CartProjection cart1 = new CartProjection() {
                @Override
                public Long getCartId() {
                    return 1L;
                }

                @Override
                public MemberProjection getMember() {
                    return () -> memberId;
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return book.getBookId();
                        }

                        @Override
                        public String getBookTitle() {
                            return book.getBookTitle();
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 2;
                }
            };

            CartProjection cart2 = new CartProjection() {
                @Override
                public Long getCartId() {
                    return 2L;
                }

                @Override
                public MemberProjection getMember() {
                    return () -> memberId;
                }

                @Override
                public BookProjection getBook() {
                    return new BookProjection() {
                        @Override
                        public Long getBookId() {
                            return book.getBookId();
                        }

                        @Override
                        public String getBookTitle() {
                            return book.getBookTitle();
                        }
                    };
                }

                @Override
                public Integer getCartAmount() {
                    return 3;
                }
            };

            List<CartProjection> cartProjections = Arrays.asList(cart1, cart2);

            when(memberRepository.existsById(memberId)).thenReturn(true);
            when(cartRepository.findAllByMemberMemberId(memberId)).thenReturn(cartProjections);

            // Act
            cartService.clearCartByMember(memberId);

            // Assert
            verify(cartRepository, times(1)).findAllByMemberMemberId(memberId);
            verify(cartRepository, times(1)).deleteById(1L);
            verify(cartRepository, times(1)).deleteById(2L);
        }

        @Test
        @DisplayName("존재하지 않는 회원의 카트를 삭제하려 할 때 예외가 발생한다")
        void clearCartByMember_MemberNotFound() {
            // Arrange
            String memberId = "nonexistent_member";

            when(memberRepository.existsById(memberId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> cartService.clearCartByMember(memberId))
                    .isInstanceOf(MemberNotFoundException.class)
                    .hasMessageContaining("Member ID: " + memberId + " not found");

            verify(cartRepository, never()).findAllByMemberMemberId(anyString());
            verify(cartRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("회원에게 카트 아이템이 없을 때 예외가 발생한다")
        void clearCartByMember_NoCarts() {
            // Arrange
            String memberId = member.getMemberId();

            when(memberRepository.existsById(memberId)).thenReturn(true);
            when(cartRepository.findAllByMemberMemberId(memberId)).thenReturn(Arrays.asList());

            // Act & Assert
            assertThatThrownBy(() -> cartService.clearCartByMember(memberId))
                    .isInstanceOf(CartNotFoundException.class)
                    .hasMessageContaining("No cart items found for Member ID: " + memberId);

            verify(cartRepository, times(1)).findAllByMemberMemberId(memberId);
            verify(cartRepository, never()).deleteById(anyLong());
        }
    }

    @Nested
    @DisplayName("updateCartItemQuantity 메서드 테스트")
    class UpdateCartItemQuantityTests {

        @Test
        @DisplayName("성공적으로 카트 아이템의 수량을 업데이트한다")
        void updateCartItemQuantity_Success() {
            // Arrange
            Long cartId = 1L;
            CartRequestDTO cartRequestDTO = new CartRequestDTO("member123", book.getBookId(), 5);

            // Cart 생성 시 'of' 메서드를 사용하여 생성
            Cart cart = Cart.of(2, member, book);
            cart.setCartId(cartId);

            when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
            when(cartRepository.save(cart)).thenReturn(cart);

            // Act
            cartService.updateCartItemQuantity(cartId, cartRequestDTO);

            // Assert
            assertThat(cart.getCartAmount()).isEqualTo(5);
            verify(cartRepository, times(1)).findById(cartId);
            verify(cartRepository, times(1)).save(cart);
        }

        @Test
        @DisplayName("존재하지 않는 카트의 수량을 업데이트하려 할 때 예외가 발생한다")
        void updateCartItemQuantity_CartNotFound() {
            // Arrange
            Long cartId = 999L;
            CartRequestDTO cartRequestDTO = new CartRequestDTO("member123", book.getBookId(), 5);

            when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> cartService.updateCartItemQuantity(cartId, cartRequestDTO))
                    .isInstanceOf(CartNotFoundException.class)
                    .hasMessageContaining("Cart ID: " + cartId + " not found");

            verify(cartRepository, times(1)).findById(cartId);
            verify(cartRepository, never()).save(any(Cart.class));
        }
    }
}
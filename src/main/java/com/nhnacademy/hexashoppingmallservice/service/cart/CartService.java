package com.nhnacademy.hexashoppingmallservice.service.cart;

import com.nhnacademy.hexashoppingmallservice.dto.cart.CartRequestDTO;

import com.nhnacademy.hexashoppingmallservice.entity.book.Book;
import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.entity.member.Member;
import com.nhnacademy.hexashoppingmallservice.exception.book.BookNotExistException;
import com.nhnacademy.hexashoppingmallservice.exception.cartException.CartAlreadyExistException;

import com.nhnacademy.hexashoppingmallservice.exception.cartException.CartNotFoundException;
import com.nhnacademy.hexashoppingmallservice.exception.member.MemberNotFoundException;


import com.nhnacademy.hexashoppingmallservice.projection.cart.CartProjection;
import com.nhnacademy.hexashoppingmallservice.repository.book.BookRepository;
import com.nhnacademy.hexashoppingmallservice.repository.cart.CartRepository;
import com.nhnacademy.hexashoppingmallservice.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Formatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Transactional
    public void createCart(CartRequestDTO cartRequestDto) {
        Member member = memberRepository.findById(String.valueOf(cartRequestDto.getMemberId())).orElseThrow(
                ()-> {
                    String errorMessage = new Formatter().format("Member ID: %s not found.", cartRequestDto.getMemberId()).toString();
                    return new MemberNotFoundException(errorMessage);
                });

        Book book = bookRepository.findById(cartRequestDto.getBookId()).orElseThrow(
                ()-> {
                    String errorMessage = new Formatter().format("Book ID: %s not found.", cartRequestDto.getBookId()).toString();
                    return new BookNotExistException(errorMessage);
                }
        );

//        if(cartRepository.findById(cartRequestDto.getBookId()).isPresent()) {
//            throw new CartAlreadyExistException(String.format("%s",cartRequestDto.getCartId()));
//        }

        Cart cart = Cart.of(
                cartRequestDto.getCartAmount(),
                member,
                book
        );

        cartRepository.save(cart);

    }

    //전체 카트 목록을 조회할 이유가 없음
//
//    @Transactional
//    public List<Cart> getCarts() {
//        return cartRepository.findAll();
//    }

    @Transactional
    public CartProjection getCart(Long cartId) {
        return cartRepository.findByCartId(cartId).orElseThrow(
                () -> new CartNotFoundException("Cart ID: %s not found".formatted(cartId))
        );
    }

    @Transactional
    public List<CartProjection> getCartByMemberId(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID: %s not found".formatted(memberId));
        }
        return cartRepository.findAllByMemberMemberId(memberId);
    }

    @Transactional
    public void deleteCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    @Transactional
    public void clearCartByMember(String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member ID: %s not found".formatted(memberId));
        }

        List<CartProjection> carts = cartRepository.findAllByMemberMemberId(memberId);
        if (carts.isEmpty()) {
            throw new CartNotFoundException("No cart items found for Member ID: " + memberId);
        }

        for (CartProjection cart : carts) {
            cartRepository.deleteById(cart.getCartId());
        }
    }

    @Transactional
    public void updateCartItemQuantity(Long cartId, CartRequestDTO cartRequestDto) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new CartNotFoundException("Cart ID: %s not found".formatted(cartId))
        );

        cart.setCartAmount(cartRequestDto.getCartAmount());

        cartRepository.save(cart);
    }




}

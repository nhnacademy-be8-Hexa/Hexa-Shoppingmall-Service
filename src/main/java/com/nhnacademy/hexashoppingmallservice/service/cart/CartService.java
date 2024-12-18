package com.nhnacademy.hexashoppingmallservice.service.cart;

import com.nhnacademy.hexashoppingmallservice.dto.Cart.CartRequestDTO;
import com.nhnacademy.hexashoppingmallservice.entity.Member;
import com.nhnacademy.hexashoppingmallservice.entity.cart.Cart;
import com.nhnacademy.hexashoppingmallservice.exception.CartException.CartAlreadyExistException;
import com.nhnacademy.hexashoppingmallservice.exception.MemberNotFoundException;
import com.nhnacademy.hexashoppingmallservice.repository.Cart.CartRepository;
import com.nhnacademy.hexashoppingmallservice.repository.MemberRepository;
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
    public Cart createCart(CartRequestDTO cartRequestDto) {
        Member member = memberRepository.findById(Long.parseLong(cartRequestDto.getMemberId())).orElseThrow(
                ()-> {
                    String errorMessage = new Formatter().format("Member ID: %s not found.", cartRequestDto.getMemberId()).toString();
                    return new MemberNotFoundException(errorMessage);
                });

        Book book = bookRepository.findById(Long.parseLong(cartRequestDto.getBookId())).orElseThrow(
                ()-> {
                    String errorMessage = new Formatter().format("Book ID: %s not found.", cartRequestDto.getBookId()).toString();
                    return new BookNotFoundException(errorMessage);
                }
        );

        if(cartRepository.findById(cartRequestDto.getBookId()).isPresent()) {
            throw new CartAlreadyExistException(String.format("%s",cartRequestDto.getCartId()));
        }

        Cart cart = new Cart(
                cartRequestDto.getCartId(),
                cartRequestDto.getCartAmount(),
                member,
                book
        );

        return cartRepository.save(cart);

    }

    @Transactional
    public List<Cart> findAllCarts() {
        return cartRepository.findAll();
    }



}
package com.nhnacademy.hexashoppingmallservice.config;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CustomPageableResolver implements HandlerMethodArgumentResolver {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 10;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
        String pageParam = webRequest.getParameter("page");
        String sizeParam = webRequest.getParameter("size");


        int page = (pageParam != null) ? Integer.parseInt(pageParam) : DEFAULT_PAGE;
        int size = (sizeParam != null) ? Integer.parseInt(sizeParam) : DEFAULT_SIZE;

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        return PageRequest.of(page, size);
    }
}

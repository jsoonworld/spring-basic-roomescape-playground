package roomescape.login;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Optional;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;
    private final LoginService loginService;

    public LoginMemberArgumentResolver(JwtUtil jwtUtil, LoginService loginService) {
        this.jwtUtil = jwtUtil;
        this.loginService = loginService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new IllegalStateException("[ERROR] 요청 정보를 처리하는 중 문제가 발생했습니다.");
        }

        Cookie[] cookies = request.getCookies();
        String token = Optional.ofNullable(cookies)
                .flatMap(c -> Arrays.stream(c)
                        .filter(cookie -> "token".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 인증 토큰을 찾을 수 없습니다."));

        Long memberId = Long.valueOf(jwtUtil.getSubject(token));

        return loginService.findLoginMember(memberId);
    }
}

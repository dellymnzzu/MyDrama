package com.MyDrama.config;


import com.MyDrama.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;  // 추가를 해줘야한다.


    @Bean // 스프링 컨테이너에 올라가는 객체 (Bean)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers("/ws/**", "/topic/**", "/app/**", "/member/signin")
                .ignoringRequestMatchers("/create-room", "/chat/**", "/admin/**", "/check-email", "/api/chatbot/**","/itemImg/**")
                .ignoringRequestMatchers("/member/find-email", "/member/find-password")
                .ignoringRequestMatchers("/member/resetPassword")
                .ignoringRequestMatchers("/comment/**")  // 댓글 API 요청에 대해 CSRF 검사 제외
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        )
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**","/buy/**", "/img/**","/crawling/**", "/favicon.ico","/qna/list", "/error").permitAll()
                .requestMatchers("/", "/member/**", "/item/**","/notice/**","/items/**", "/itemImg/**", "/banner/**", "/notice/**", "/about/**", "/chatbot/**").permitAll()
                .requestMatchers("/chat/**", "/create-room").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/templates/cart/**").authenticated()
                .requestMatchers("/comment/list/**").permitAll() // 댓글 목록 조회는 모든 사용자에게 허용
                .requestMatchers("/comment/new").authenticated() // 댓글 작성은 인증된 사용자만
                .requestMatchers("/comment/delete/**").authenticated() // 댓글 삭제는 인증된 사용자만
                .requestMatchers("/cart/**").authenticated()  // 장바구니는 인증된 사용자만
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/member/**")
                .ignoringRequestMatchers("/comment/**")
            );
        //로그인을 할 경우 로그인 페이지가 나오는 경우는 관리자가 하는거지만, submit으로 로그인을 하게 된다면, formLogin으로 들어가게 된다.
        http.formLogin(formLogin -> formLogin  // form 로그인 경우 여기로 온다.
            .loginPage("/member/signin")  // 로그인 페이지는 /member/signin이다. (url 매핑이라고 생각하면된다.)
            .defaultSuccessUrl("/")  // 로그인 성공시
            .usernameParameter("userId")  // 로그인에 필요한 파라미터("userId")이다.
            .failureUrl("/member/signin/error")  // 실패시 이동하는 매핑이다.
        )
        .logout(logout -> logout  // 로그아웃일 경우
            .logoutRequestMatcher(new AntPathRequestMatcher("member/logout"))
            .logoutSuccessUrl("/")
        ).oauth2Login(oauthLogin -> oauthLogin  // 인증로그인은 이 곳으로 가게된다. 결과만 이곳으로 온다.
                    .defaultSuccessUrl("/")
                    .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService)));


    //exceptionHandling 예외처리 핸들링 예외처리가 발생하면 CustomAuthenticationEntryPoint 클래스 위임
    http.exceptionHandling(exception -> exception
            .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

    return http.build();
}
@Bean // 패스워드 암호화 해주는 객체
public static PasswordEncoder passwordEncoder(){
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
}

@Autowired
// AuthenticationManagerBuilder -> UserDetailService를 구현하고 그 객체 MemberService 지정과 동시에 비밀번호를 암호화 암호화 한 후 MemberService에 들어가서 LoadUserByUsername을 실행시켜준다.
public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
}
}

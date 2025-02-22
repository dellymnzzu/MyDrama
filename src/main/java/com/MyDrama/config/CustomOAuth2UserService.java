package com.MyDrama.config;

import com.MyDrama.dto.SessionUser;
import com.MyDrama.entity.SnsMember;
import com.MyDrama.repository.SnsMemberRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    @Autowired
    private SnsMemberRepository snsMemberRepository;
    @Autowired
    private HttpSession httpSession;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = oAuth2UserService.loadUser(oAuth2UserRequest);

        // naver, kakao, google id
        //registrationId : kakao
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId : "+registrationId);

        //userNameAttributeName : id
        //spring.security.oauth2.client.provider.kakao.user-name-attribute = id
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        System.out.println("userNameAttributeName : "+userNameAttributeName);

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName
                , oAuth2User.getAttributes());

        SnsMember snsMember = saveOrUpdate(attributes);
        httpSession.setAttribute("user",new SessionUser(snsMember)); // 로그인 세팅

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                , attributes.getAttributes()
                , attributes.getNameAttributeKey()
        );
    }
    private SnsMember saveOrUpdate(OAuthAttributes attributes){

        SnsMember user = snsMemberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        return snsMemberRepository.save(user);
    }
}


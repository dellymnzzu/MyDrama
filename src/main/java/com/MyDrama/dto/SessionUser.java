package com.MyDrama.dto;

import com.MyDrama.entity.Member;
import com.MyDrama.entity.SnsMember;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SessionUser implements Serializable {

    private String name;
    private String email;
    private String picture;

    @Builder
    public SessionUser(SnsMember snsMember){
        this.name = snsMember.getName();
        this.email = snsMember.getEmail();
        this.picture = snsMember.getPicture();
    }


}



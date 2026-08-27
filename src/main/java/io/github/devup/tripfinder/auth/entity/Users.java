package io.github.devup.tripfinder.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 엔티티가 Auditing감시대상등록 created_at, updated_at 자동 갱신용
@Table(name="users",uniqueConstraints = {
        @UniqueConstraint(name="UK_login_email",columnNames = "login_email"),
        @UniqueConstraint(name="UK_provider_social_uid",columnNames = {"provider","social_uid"})
})
public class Users {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="login_email", length = 100)
    private String loginEmail;

    @Column(name="login_password")
    private String loginPassword;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'local'")
    private String provider = "local";

    @Column(name="social_uid",length = 100)
    private String socialUid;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Column(length = 50)
    private String location;

    @Column(name="profile_url",length = 500)
    private String profileUrl;

    @Column(name="age_group", length = 20)
    private String ageGroup;

    @Column(name="gender", columnDefinition = "TINYINT")
    private Integer gender;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Boolean enabled = true;

    @Column(name="refresh_token", length = 500)
    private String refreshToken;

    @Column(nullable = false, length = 20)
    @ColumnDefault("'USER'")
    private String role = "USER";

    @CreatedDate //저장되는시점에 자동으로 시간기록해주는거
    @Column(name="created_at",nullable = false,updatable = false) //updatable -> 최초값저장하고 그뒤로는 수정불가
    private LocalDateTime createdAt;

    @LastModifiedDate //수정(UPDATE)될 때마다 해당 시점의 날짜와 시간을 자동으로 최신화
    @Column(name="updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Users(String loginEmail, String loginPassword, String provider, String socialUid,
                 String nickname, String location, String profileUrl, String ageGroup,
                 Integer gender, String role) {
        this.loginEmail = loginEmail;
        this.loginPassword = loginPassword;
        this.provider = provider != null ? provider : "local";
        this.socialUid = socialUid;
        this.nickname = nickname;
        this.location = location;
        this.profileUrl = profileUrl;
        this.ageGroup = ageGroup;
        this.gender = gender;
        this.role = role != null ? role : "USER";
        this.enabled = true;
    }

    //RefreshToken 갱신용 비즈니스 메서드
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


}

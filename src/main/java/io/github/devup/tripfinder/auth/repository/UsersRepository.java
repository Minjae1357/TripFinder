package io.github.devup.tripfinder.auth.repository;

import io.github.devup.tripfinder.auth.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByProviderAndSocialUid(String provider, String socialUid);
    Optional<Users> findByLoginEmailAndProvider(String loginEmail,String provider);
    boolean existsByLoginEmailAndProvider(String email,String provider); // 조건을 만족하는 데이터 존재 시 종료
}

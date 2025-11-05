package com.adhd.ad_hell.domain.auth.command.repository;


import com.adhd.ad_hell.domain.auth.command.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,String> {



}

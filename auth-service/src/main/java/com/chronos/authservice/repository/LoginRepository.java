package com.chronos.authservice.repository;

import com.chronos.authservice.entity.LoginCredential;
import com.chronos.authservice.repository.projections.LoginByEmailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginRepository extends JpaRepository<LoginCredential, UUID> {
    @Query("select l from LoginCredential l where l.email = :email")
    Optional<LoginCredential> findByEmailWithoutProjection(String email);

    @Query("""
            select
                l.id as id,
                l.email as email,
                l.passwordHash as password,
                l.role as role,
                l.displayEmployeeId as displayEmployeeId
            from LoginCredential l
                where l.email = :email
            """)
    Optional<LoginByEmailView> findByEmailView(@Param("email") String email);
}
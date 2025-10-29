package com.chronos.employeeservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "teams")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Team extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "team_id", length = 20, nullable = false, unique = true)
    private String teamId;

    @OneToOne
    @JoinColumn(name = "team_manager_id", unique = true)
    private Employee teamManager;

    @Column(name = "team_name", length = 100, nullable = false)
    private String teamName;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees;
}

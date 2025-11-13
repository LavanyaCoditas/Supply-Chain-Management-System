package com.project.supply.chain.management.entity;


import com.project.supply.chain.management.constants.ToolIssuanceStatus;
import jakarta.persistence.*;
        import lombok.*;

@Entity
@Table(name = "tool_issuance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ToolIssuance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "request_id")
    private ToolRequest request;

    @ManyToOne
    @JoinColumn(name = "tool_id")
    private Tool tool;

    @Enumerated(EnumType.STRING)
    private ToolIssuanceStatus status = ToolIssuanceStatus.ISSUED;

}

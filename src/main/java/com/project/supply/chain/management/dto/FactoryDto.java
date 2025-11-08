package com.project.supply.chain.management.dto;

import com.project.supply.chain.management.constants.Account_Status;
import com.project.supply.chain.management.entity.CentralOffice;
import com.project.supply.chain.management.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FactoryDto {

        private String name;
        private String city;
        private String address;
         // will map to CentralOffice entity
        private String plantHeadEmail; // optional field


}

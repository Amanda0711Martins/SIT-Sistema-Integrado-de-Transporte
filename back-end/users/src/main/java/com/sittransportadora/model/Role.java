package com.sittransportadora.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Getter
    public enum Values{
        ROLE_ADMIN(1L),
        ROLE_USER(2L);

        long value;

        Values(Long value){
            this.value = value;
        }
    }

}

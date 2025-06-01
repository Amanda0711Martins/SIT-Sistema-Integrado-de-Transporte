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
        ADMIN(1L),
        USER(2L);

        long value;

        Values(Long value){
            this.value = value;
        }
    }

}

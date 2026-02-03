package com.example.SpringAudiJPA.entity;

import com.example.SpringAudiJPA.listener.AuditEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@EntityListeners(AuditEntityListener.class)
public class product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//autoincremental
    private Integer id ;
    private String name;
    private String description;
    private BigDecimal price;//siempre que se maneja dinero con BigDecimasl

   //SEGUIMIENTO Y AUDITORIA
    private String operation ;
    @Column(name = "date_event")
    private LocalDate dateEvent ;
    /*   METODOS PARA HACER AUDITORIAS
    @PrePersist
    public void onPrePersist(){
     audit("INSERT");
    }
    @PreUpdate
    public void onPreUpdate(){

        audit("UPDATE");
    }


    public void audit(String operation){
        setOperation(operation);
        setDateEvent(LocalDate.now());
    }*/

}

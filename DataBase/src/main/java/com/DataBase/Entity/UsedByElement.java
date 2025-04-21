package com.DataBase.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "used_by_element")
@NoArgsConstructor
@Setter
@Getter
public class UsedByElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_used_by_element")
    private int id;

    @Column(name = "used_first")
    private String usedFirst;

    @Column(name = "id_element")
    private int idElement;
 
    @Column(name = "id_variable")
    private int idVariable;
 
}

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
@Table(name = "used_by_method")
@NoArgsConstructor
@Setter
@Getter
public class UsedByMethod {
     
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_used_by_method")
    private int id;

    @Column(name = "modify")
    private boolean modify;

    @Column(name = "id_method")
    private int idMethod;
 
    @Column(name = "id_variable")
    private int idVariable;
}

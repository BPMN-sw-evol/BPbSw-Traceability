package com.DataBase.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contained_in")
@NoArgsConstructor
@Setter
@Getter
public class ContainedIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contained_in")
    private int id;

    @Column(name = "id_variable")
    private int idVariable;

    @Column(name = "id_data_container")
    private int idDataContainer;
}

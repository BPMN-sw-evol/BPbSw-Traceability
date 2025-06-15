package com.example.api.apitraceability.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "data_container")
@NoArgsConstructor
@Setter
@Getter
public class DataContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_data_container")
    private int id;

    @Column(name = "name_container")
    private String nameContainer;

    // Relación uno a muchos
    @OneToMany(mappedBy = "dataContainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContainedIn> containedIns = new ArrayList<>();

    // Relación muchos a uno
    @ManyToOne
    @JoinColumn(name = "id_project", nullable = false)
    private Project project;

}

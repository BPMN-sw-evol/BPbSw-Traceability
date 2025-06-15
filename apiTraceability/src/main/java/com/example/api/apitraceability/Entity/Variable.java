package com.example.api.apitraceability.Entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "variable")
@NoArgsConstructor
@Setter
@Getter
public class Variable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variable")
    private int id;

    @Column(name = "variable_name")
    private String variableName;

    // Relación uno a muchos
    @OneToMany(mappedBy = "variable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContainedIn> containedIns = new ArrayList<>();

    // Relación uno a muchos
    @OneToMany(mappedBy = "variable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsedByMethod> usedByMethods = new ArrayList<>();

    // Relación uno a muchos
    @OneToMany(mappedBy = "variable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsedByElement> usedByElements = new ArrayList<>();

    // Relación muchos a uno
    @ManyToOne
    @JoinColumn(name = "id_history", nullable = false)
    private History history;

}

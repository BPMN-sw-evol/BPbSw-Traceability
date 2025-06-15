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
@Table(name = "method")
@NoArgsConstructor
@Setter
@Getter
public class Method {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_method")
    private int id;

    @Column(name = "name_method")
    private String nameMethod;

    // Relación uno a muchos
    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsedByMethod> usedByMethods = new ArrayList<>();

    // Relación muchos a uno
    @ManyToOne
    @JoinColumn(name = "id_class", nullable = false)
    private Class classe;

 
}

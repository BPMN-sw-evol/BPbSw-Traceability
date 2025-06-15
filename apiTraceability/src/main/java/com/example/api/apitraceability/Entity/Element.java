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
@Table(name = "element")
@NoArgsConstructor
@Setter
@Getter
public class Element {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_element")
    private int id;

    @Column(name = "element_name")
    private String elementName;

    @Column(name = "lane")
    private String lane;

    // Relación uno a muchos
    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsedByElement> usedByElements = new ArrayList<>();

    // Relación muchos a uno
    @ManyToOne
    @JoinColumn(name = "id_element_type", nullable = false)
    private ElementType elementType;

    // Relación muchos a uno
    @ManyToOne
    @JoinColumn(name = "id_process", nullable = false)
    private Process process;
 
}

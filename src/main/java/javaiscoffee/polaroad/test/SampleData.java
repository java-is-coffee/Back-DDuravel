package javaiscoffee.polaroad.test;

import lombok.*;

import jakarta.persistence.*;

@Getter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
public class SampleData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String detail;
}

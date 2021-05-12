package dev.c20.rules.engine.storage.entities.adds;

import dev.c20.rules.engine.storage.entities.Storage;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name= "C20_STG_WORD")
@Accessors( chain = true )
@Setter
@Getter
public class Word implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="C20_ID")
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn( name="C20_STORAGE")
    private Storage parent;

    @Column(name="C20_WORD", length = 60)
    private String word;

}

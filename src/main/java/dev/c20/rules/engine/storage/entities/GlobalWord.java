package dev.c20.rules.engine.storage.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name= "C20_GLOBAL_WORD")
@Accessors( chain = true )
@Setter
@Getter
public class GlobalWord implements Serializable {

    @Id
    @Column(name="C20_WORD")
    private String word;

}

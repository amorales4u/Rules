package dev.c20.rules.storage.entities.adds;


import dev.c20.rules.storage.entities.Storage;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name= "C20_STG_VALUE")
public class Value {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="C20_ID")
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn( name="C20_STORAGE")
    private Storage parent;

    @Column(name="C20_NAME")
    private String name;

    @Column(name="C20_STR_VALUE")
    private String value;

    @Column(name="C20_NUM_VALUE", precision = 6)
    private Double doubleValue;

    @Column(name="C20_DATE_VALUE")
    private Date dateValue;

    @Column(name="C20_LONG_VALUE")
    private Long longValue;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return id.equals(value.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public Value setId(Long id) {
        this.id = id;
        return this;
    }

    public Storage getParent() {
        return parent;
    }

    public Value setParent(Storage parent) {
        this.parent = parent;
        return this;
    }

    public String getName() {
        return name;
    }

    public Value setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Value setValue(String value) {
        this.value = value;
        return this;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public Value setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
        return this;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public Value setDateValue(Date dateValue) {
        this.dateValue = dateValue;
        return this;
    }

    public Long getLongValue() {
        return longValue;
    }

    public Value setLongValue(Long longValue) {
        this.longValue = longValue;
        return this;
    }


}

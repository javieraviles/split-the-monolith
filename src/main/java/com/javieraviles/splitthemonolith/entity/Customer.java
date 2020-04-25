package com.javieraviles.splitthemonolith.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
@Entity(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Size(min = 3, max = 20)
    private String name;

    @PositiveOrZero
    private double credit;

    public Customer() {
    }

    public Customer(final String name, final double credit) {
        this.name = name;
        this.credit = credit;
    }

    public String getName() {
        return name;
    }

    public double getCredit() {
        return credit;
    }
}
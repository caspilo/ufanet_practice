package org.example.entity;


import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="tasks")
public class Task {

    @Id
    @GeneratedValue
    private int id;

    private String type;

    

}

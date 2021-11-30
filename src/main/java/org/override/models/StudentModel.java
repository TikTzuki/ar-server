package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.override.core.models.HyperBody;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "student_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentModel implements HyperBody {
    @Id
    public String studenId;

    public Double avgScore;

    public Integer course;

    public String subject;

    public String speciality;

    @Override
    public String toJson() {
        return null;
    }
}

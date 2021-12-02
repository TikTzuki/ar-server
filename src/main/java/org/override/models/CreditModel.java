package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.override.core.models.HyperBody;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "credit_model")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditModel implements HyperBody {
    @Id
    public String subjectId;
    public String subjectName;
    public Integer creditsCount;

    public String courses;

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}

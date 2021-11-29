package org.override.models;

import jdk.jfr.Enabled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.override.core.models.HyperBody;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}

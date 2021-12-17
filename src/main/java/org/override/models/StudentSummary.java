package org.override.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSummary {
    public String id;
    public String name;
    public String gender;
    public String placeOfBirth;
    public String dateOfBirth;
    public String classes;
    public String subject;
    public String speciality;

}

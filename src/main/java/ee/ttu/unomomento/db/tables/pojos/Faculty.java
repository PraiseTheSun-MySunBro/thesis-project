/*
 * This file is generated by jOOQ.
*/
package ee.ttu.unomomento.db.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * 1 -- School of Business and Governance, 2 -- School of Engineering, 3 -- 
 * School of Information Technologies, 4 -- School of Science, 5 -- Estonian 
 * Maritime Academy
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Faculty implements Serializable {

    private static final long serialVersionUID = 1652784998;

    private final Short  facultyCode;
    private final String eeName;
    private final String enName;

    public Faculty(Faculty value) {
        this.facultyCode = value.facultyCode;
        this.eeName = value.eeName;
        this.enName = value.enName;
    }

    public Faculty(
        Short  facultyCode,
        String eeName,
        String enName
    ) {
        this.facultyCode = facultyCode;
        this.eeName = eeName;
        this.enName = enName;
    }

    public Short getFacultyCode() {
        return this.facultyCode;
    }

    public String getEeName() {
        return this.eeName;
    }

    public String getEnName() {
        return this.enName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Faculty (");

        sb.append(facultyCode);
        sb.append(", ").append(eeName);
        sb.append(", ").append(enName);

        sb.append(")");
        return sb.toString();
    }
}

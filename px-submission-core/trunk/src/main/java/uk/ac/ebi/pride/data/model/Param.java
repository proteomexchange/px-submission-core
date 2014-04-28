package uk.ac.ebi.pride.data.model;

import java.io.Serializable;

/**
 * {@code Param} is an abstract class for defining parameters,
 * it is the foundation of both cv param and user param
 *
 * @author Rui Wang
 * @version $Id$
 */
public class Param implements Serializable{
    private String name;
    private String value;

    public Param(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Param)) return false;

        Param param = (Param) o;

        if (!name.equals(param.name)) return false;
        if (value != null ? !value.equals(param.value) : param.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "[, , " + name + "," + value + "]";
    }
}

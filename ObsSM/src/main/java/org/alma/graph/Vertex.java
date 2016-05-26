package org.alma.graph;

import java.io.Serializable;
import java.util.Objects;

public class Vertex implements Serializable{

    private String state;
    private Object payload;

    public Vertex() {
    }

    public Vertex(String state, Object payload) {
        this.state = state;
        this.payload = payload;
    }
    
    public Vertex(String state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.state);
        hash = 83 * hash + Objects.hashCode(this.payload);
        return hash;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vertex other = (Vertex) obj;
        return Objects.equals(this.state, other.state);
    }

    @Override
    public String toString() {
        return state;
    }

}

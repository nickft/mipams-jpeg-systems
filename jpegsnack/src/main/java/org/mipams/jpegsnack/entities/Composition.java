package org.mipams.jpegsnack.entities;

import java.util.ArrayList;
import java.util.List;

public class Composition {
    Integer id;
    int noOfObjects;
    List<Integer> objectIds = new ArrayList<>();

    public long computeSizeInBytes() {
        long result = 0;

        if (idExists()) {
            result += 1;
        }

        result += 1;

        result += objectIds.size();

        return result;
    }

    private boolean idExists() {
        return id != null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setNoOfObjects(int noOfObjects) {
        this.noOfObjects = noOfObjects;
    }

    public int getNoOfObjects() {
        return noOfObjects;
    }

    public void setObjectIds(List<Integer> objectIds) {
        this.objectIds.clear();
        this.objectIds.addAll(objectIds);
    }

    public List<Integer> getObjectIds() {
        return objectIds;
    }
}

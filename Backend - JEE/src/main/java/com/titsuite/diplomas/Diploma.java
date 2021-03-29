package com.titsuite.diplomas;

import com.titsuite.utils.JsonSerializable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Diploma implements JsonSerializable {

    private long id;
    private String name;
    private Date acquisitionDate;
    private String field;
    private long freelancerRef;

    public Diploma() {}

    public Diploma(long id, String name, Date acquisitionDate, String field, long freelancerRef) {
        setId(id);
        setName(name);
        setAcquisitionDate(acquisitionDate);
        setField(field);
        setFreelancerRef(freelancerRef);
    }

    public long getId() { return this.id; }
    
    public void setId(long id) { this.id = id; }
    
    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public Date getAcquisitionDate() { return this.acquisitionDate; }

    public void setAcquisitionDate(Date acquisitionDate) { this.acquisitionDate = acquisitionDate; }

    public String getField() { return this.field; }

    public void setField(String field) { this.field = field; }

    public long getFreelancerRef() { return this.freelancerRef; }

    public void setFreelancerRef(long freelancerRef) { this.freelancerRef = freelancerRef; }

    @Override
    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", getId());
        jsonObject.put("name", getName());
        jsonObject.put("acquisitionDate", getAcquisitionDate().getTime());
        jsonObject.put("field", getField());
        return jsonObject;
    }

}

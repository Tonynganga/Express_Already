package com.tony.directions_app.Models;

import java.io.Serializable;

public class Model implements Serializable {

    String CCurrentLocality, CDestinationName, PSourceName,
            PDestinationName, PDistance, PDate, CDistance, CDate;

    public String getCCurrentLocality() {
        return CCurrentLocality;
    }

    public String getCDestinationName() {
        return CDestinationName;
    }

    public String getPSourceName() {
        return PSourceName;
    }

    public String getPDestinationName() {
        return PDestinationName;
    }

    public String getPDistance() {
        return PDistance;
    }

    public String getPDate() { return PDate; }

    public String getCDistance() {
        return CDistance;
    }

    public String getCDate() {
        return CDate;
    }
}

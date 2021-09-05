package com.zvolinskiy.odariabot;

/** A class for processing json data from site https://bkport.com/ */
public class ContainerBkp {
    private boolean searchStatus;
    private String statusText;
    private String arrivalDate;
    private String containerId;

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public boolean isSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(boolean searchStatus) {
        this.searchStatus = searchStatus;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String  getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
}

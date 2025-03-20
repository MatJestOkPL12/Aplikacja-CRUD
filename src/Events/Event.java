package Events;

import client.Client;

import java.util.ArrayList;
import java.util.List;

public class Event {

    public Event(int idEvent, String name){
        this.name = name;
        this.idEvent = idEvent;
    }
    private int idEvent;
    private String name;
    public List<Integer> interestedId = new ArrayList<>();
    public List<Integer> confirmedClients = new ArrayList<>();
    private int organizerId;
    private int offerId;
    private int needed;
    private String parameters;
    private boolean tookPlace = false;
    private boolean readyToRealize = false;
    private boolean organizerAccepted = false;

    public int getIdEvent() {
        return idEvent;
    }

    public String getName() {
        return name;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public int getNeeded() {
        return needed;
    }

    public void setNeeded(int needed) {
        this.needed = needed;
    }
    public void setReadyToRealize(){
        readyToRealize = true;
    }
    public boolean getReadyToRealize(){
        return readyToRealize;
    }
    public void organizerAccept(){
        organizerAccepted = true;
    }
    public boolean getOrganizerAccepted(){
        return organizerAccepted;
    }
    public void tookPlace(){
        tookPlace = true;
    }

    public boolean isTookPlace() {
        return tookPlace;
    }
}

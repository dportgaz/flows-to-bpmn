package org.bpmn.step_one.collaboration;

import org.bpmn.flowsObjects.AbstractObjectType;
import org.bpmn.randomidgenerator.RandomIdGenerator;
import org.bpmn.step_one.collaboration.participant.Object;

import org.bpmn.step_one.collaboration.participant.User;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bpmn.fillxml.ExecSteps.doc;

public class Collaboration {

    String id;

    Element elementCollaboration;

    public static ArrayList<Object> objects = new ArrayList<>();

    public static ArrayList<User> users = new ArrayList<>();


    public Collaboration() {
        this.id = "Collaboration_" + RandomIdGenerator.generateRandomUniqueId(6);
        this.elementCollaboration = doc.createElement("bpmn:collaboration");
        setElementCollaboration();
    }

    // fill attributes of collaboration Element
    private void setElementCollaboration() {
        this.elementCollaboration.setAttribute("id", this.id);
    }

    public Element getElementCollaboration() {
        return elementCollaboration;
    }

    public String getId() {
        return this.id;
    }

    public void setParticipantsOne(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects) {

        for (String key : objectTypeObjects.keySet()) {
            objectTypeObjects.get(key).forEach(obj -> {

                if (obj != null && obj.getMethodName().equals("UpdateName")) {

                    String participantName = (String) obj.getParameters().get(0);

                    // work around for json double entry bug in flows json
                    if (!containsParticipant(participantName)) {

                        Object participant = new Object(this, key, participantName, objectTypeObjects);
                        objects.add(participant);

                        // add participant to collaboration element
                        elementCollaboration.appendChild(participant.getParticipantElement());
                    }

                }

            });
        }
        for (Object participant : objects) {
            participant.setProcessRef(objectTypeObjects);
        }
    }

    public void setParticipantsTwo(HashMap<String, ArrayList<AbstractObjectType>> objectTypeObjects, HashMap<String, ArrayList<AbstractObjectType>> userTypeObjects) {

        for (String key : userTypeObjects.keySet()) {
            userTypeObjects.get(key).forEach(obj -> {
                if (obj != null && obj.getMethodName().equals("UpdateGlobalRoleName")) {

                    String name = (String) obj.getParameters().get(1);
                    Double updatedEntityId = obj.getUpdatedEntityId();
                    User participant = new User(this, key, name, updatedEntityId, objectTypeObjects, userTypeObjects);

                    if (!users.contains(participant)) {
                        users.add(participant);
                    }

                    // add participant to collaboration element
                    elementCollaboration.appendChild(participant.getParticipantElement());

                }
            });
        }

        for (User participant : users) {
            participant.setProcessRef(objectTypeObjects, userTypeObjects);
        }

    }

    private boolean containsParticipant(String participantName) {

        for (Object participant : objects) {
            if (participant.getName().equals(participantName)) {
                return true;
            }
        }
        return false;

    }

    public static User getUser(Double id) {

        for (User user : users) {
            System.out.println("USERNAME: " + user.getName() + " , UID: " + user.getUpdatedEntityId() + " , id: " + id);
            if (user.getUpdatedEntityId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}

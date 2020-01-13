package ir.saga.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import ir.saga.orchestration.DestinationAndResource;
import ir.saga.orchestration.SerializedSagaData;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Set;

@Document(collection = "sagaInstance")
public class SagaInstance {
    @Id
    private String id;
    private Instant createdDate;

    private String createdBy;

    private String sagaType;

    private String lastRequestId;

    private SerializedSagaData serializedSagaData;

    private String stateName;

    private String rejectionReason;

    private Set<DestinationAndResource> destinationsAndResources;
    @Field()
    private Boolean endState = false;
    @Field()
    private Boolean compensating = false;

    public void setSagaType(String sagaType) {
        this.sagaType = sagaType;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public SagaInstance(){

    }
    public SagaInstance(String sagaType, String sagaId, String stateName, String lastRequestId, SerializedSagaData serializedSagaData, Set<DestinationAndResource> destinationsAndResources,Instant createdDate,String createdBy) {
        this.sagaType = sagaType;
        this.id = sagaId;
        this.stateName = stateName;
        this.lastRequestId = lastRequestId;
        this.serializedSagaData = serializedSagaData;
        this.destinationsAndResources = destinationsAndResources;
        this.createdDate = createdDate;
        this.createdBy = createdBy ;
    }

    public SerializedSagaData getSerializedSagaData() {
        return serializedSagaData;
    }

    public void setSerializedSagaData(SerializedSagaData serializedSagaData) {
        this.serializedSagaData = serializedSagaData;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSagaType() {
        return sagaType;
    }

    public String getLastRequestId() {
        return lastRequestId;
    }

    public void setLastRequestId(String requestId) {
        this.lastRequestId = requestId;
    }

    public void addDestinationsAndResources(Set<DestinationAndResource> destinationAndResources) {
        this.destinationsAndResources.addAll(destinationAndResources);
    }

    public Set<DestinationAndResource> getDestinationsAndResources() {
        return destinationsAndResources;
    }

    public void setDestinationsAndResources(Set<DestinationAndResource> destinationsAndResources) {
        this.destinationsAndResources = destinationsAndResources;
    }

    public Boolean getEndState() {
        return endState;
    }

    public void setEndState(Boolean endState) {
        this.endState = endState;
    }

    public Boolean getCompensating() {
        return compensating;
    }

    public void setCompensating(Boolean compensating) {
        this.compensating = compensating;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

package com.example.springjwt.models;

import java.util.List;

public class ShareObjectiveDTO {
    private Long objectiveId;
    private List<Long> userIds;

    public Long getObjectiveId() {
        return objectiveId;
    }

    public void setObjectiveId(Long objectiveId) {
        this.objectiveId = objectiveId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }
}

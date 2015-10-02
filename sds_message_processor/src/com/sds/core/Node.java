package com.sds.core;

/**
 *
 * @author kamochu
 */
public class Node {

    int id;
    private String message;
    private String validationRule;
    private String validationFailureCourse;
    private String dbFiledName;
    private int newRegStatus;
    private int pauseNode;
    private int finalNode;
    private String pauseMessage;
    private String finalMessage;
    private int nextNode;
    private String matchingQuery;

    public Node() {
    }

    public Node(int id, String message, String validationRule, String validationFailureCourse, String dbFiledName, int newRegStatus, int pauseNode, int finalNode, String pauseMessage, String finalMessage, int nextNode, String matchingQuery) {
        this.id = id;
        this.message = message;
        this.validationRule = validationRule;
        this.validationFailureCourse = validationFailureCourse;
        this.dbFiledName = dbFiledName;
        this.newRegStatus = newRegStatus;
        this.pauseNode = pauseNode;
        this.finalNode = finalNode;
        this.pauseMessage = pauseMessage;
        this.finalMessage = finalMessage;
        this.nextNode = nextNode;
        this.matchingQuery = matchingQuery;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValidationRule() {
        return validationRule;
    }

    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }

    public String getValidationFailureCourse() {
        return validationFailureCourse;
    }

    public void setValidationFailureCourse(String validationFailureCourse) {
        this.validationFailureCourse = validationFailureCourse;
    }

    public String getDbFiledName() {
        return dbFiledName;
    }

    public void setDbFiledName(String dbFiledName) {
        this.dbFiledName = dbFiledName;
    }

    public int getNewRegStatus() {
        return newRegStatus;
    }

    public void setNewRegStatus(int newRegStatus) {
        this.newRegStatus = newRegStatus;
    }

    public int getPauseNode() {
        return pauseNode;
    }

    public void setPauseNode(int pauseNode) {
        this.pauseNode = pauseNode;
    }

    public int getFinalNode() {
        return finalNode;
    }

    public void setFinalNode(int finalNode) {
        this.finalNode = finalNode;
    }

    public String getPauseMessage() {
        return pauseMessage;
    }

    public void setPauseMessage(String pauseMessage) {
        this.pauseMessage = pauseMessage;
    }

    public String getFinalMessage() {
        return finalMessage;
    }

    public void setFinalMessage(String finalMessage) {
        this.finalMessage = finalMessage;
    }

    public int getNextNode() {
        return nextNode;
    }

    public void setNextNode(int nextNode) {
        this.nextNode = nextNode;
    }

    public String getMatchingQuery() {
        return matchingQuery;
    }

    public void setMatchingQuery(String matchingQuery) {
        this.matchingQuery = matchingQuery;
    }

    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", message=" + message + ", validationRule=" + validationRule + ", validationFailureCourse=" + validationFailureCourse + ", dbFiledName=" + dbFiledName + ", newRegStatus=" + newRegStatus + ", pauseNode=" + pauseNode + ", finalNode=" + finalNode + ", pauseMessage=" + pauseMessage + ", finalMessage=" + finalMessage + ", nextNode=" + nextNode + ", matchingQuery=" + matchingQuery + '}';
    }

}

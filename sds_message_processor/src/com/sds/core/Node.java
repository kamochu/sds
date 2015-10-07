package com.sds.core;

/**
 *
 * @author kamochu
 */
public class Node {

    int id;
    private String message;
    private String validationRule;
    private String validationFailureMessage;
    private String dbFiledName;
    private int integerValue;
    private int newRegStatus;
    private int pauseNode;
    private int finalNode;
    private int nextNode;
    private String matchingQuery;

    public Node() {
    }

    public Node(int id, String message, String validationRule, String validationFailureMessage, String dbFiledName, int integerValue, int newRegStatus, int pauseNode, int finalNode, int nextNode, String matchingQuery) {
        this.id = id;
        this.message = message;
        this.validationRule = validationRule;
        this.validationFailureMessage = validationFailureMessage;
        this.dbFiledName = dbFiledName;
        this.integerValue = integerValue;
        this.newRegStatus = newRegStatus;
        this.pauseNode = pauseNode;
        this.finalNode = finalNode;
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

    public String getValidationFailureMessage() {
        return validationFailureMessage;
    }

    public void setValidationFailureMessage(String validationFailureMessage) {
        this.validationFailureMessage = validationFailureMessage;
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

    public boolean isPauseNode() {
        return (pauseNode == 1);
    }

    public void setPauseNode(int pauseNode) {
        this.pauseNode = pauseNode;
    }

    public boolean isFinalNode() {
        return (finalNode == 1);
    }

    public void setFinalNode(int finalNode) {
        this.finalNode = finalNode;
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

    public int getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(int integerValue) {
        this.integerValue = integerValue;
    }

    public boolean isFieldIntegerValue() {
        return (this.integerValue == 1);
    }

    @Override
    public String toString() {
        return "Node{" + "id=" + id + ", message=" + message + ", validationRule=" + validationRule + ", validationFailureMessage=" + validationFailureMessage + ", dbFiledName=" + dbFiledName + ", integerValue=" + integerValue + ", newRegStatus=" + newRegStatus + ", pauseNode=" + pauseNode + ", finalNode=" + finalNode + ", nextNode=" + nextNode + ", matchingQuery=" + matchingQuery + '}';
    }

}

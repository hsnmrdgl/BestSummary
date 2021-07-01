package com.example.bestsummary;

public class Summary {

    String SummaryBookID, SummaryText, SummaryTime, SummaryLike, SummaryUserID, SummaryUserMail, SummaryUsername;

    public Summary() {
    }

    public Summary(String summaryBookID, String summaryText, String summaryTime, String summaryUserID, String summaryUserMail, String summaryUsername) {
        SummaryBookID = summaryBookID;
        SummaryText = summaryText;
        SummaryTime = summaryTime;
        SummaryUserID = summaryUserID;
        SummaryUserMail = summaryUserMail;
        SummaryUsername = summaryUsername;
    }

    public String getSummaryBookID() {
        return SummaryBookID;
    }

    public void setSummaryBookID(String summaryBookID) {
        SummaryBookID = summaryBookID;
    }

    public String getSummaryText() {
        return SummaryText;
    }

    public void setSummaryText(String summaryText) {
        SummaryText = summaryText;
    }

    public String getSummaryTime() {
        return SummaryTime;
    }

    public void setSummaryTime(String summaryTime) {
        SummaryTime = summaryTime;
    }

    public String getSummaryLike() {
        return SummaryLike;
    }

    public void setSummaryLike(String summaryLike) {
        SummaryLike = summaryLike;
    }

    public String getSummaryUserID() {
        return SummaryUserID;
    }

    public void setSummaryUserID(String summaryUserID) {
        SummaryUserID = summaryUserID;
    }

    public String getSummaryUserMail() {
        return SummaryUserMail;
    }

    public void setSummaryUserMail(String summaryUserMail) {
        SummaryUserMail = summaryUserMail;
    }

    public String getSummaryUsername() {
        return SummaryUsername;
    }

    public void setSummaryUsername(String summaryUsername) {
        SummaryUsername = summaryUsername;
    }
}

package com.motishare.dozeecodeforhealth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QuestionModel {

    @SerializedName("Question")
    @Expose
    private String question;
    @SerializedName("Greeting")
    @Expose
    private String greeting;
    @SerializedName("Answers")
    @Expose
    private List<String> answers = null;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "QuestionModel{" +
                "question='" + question + '\'' +
                ", greeting='" + greeting + '\'' +
                ", answers=" + answers +
                '}';
    }
}
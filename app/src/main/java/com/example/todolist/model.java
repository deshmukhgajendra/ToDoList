package com.example.todolist;

public class model {

    String task;

    public model(String task){
        this.task=task;
    }
    public String getTask(){
        return task;
    }
    public void setTask(String task){
        this.task=task;
    }
}

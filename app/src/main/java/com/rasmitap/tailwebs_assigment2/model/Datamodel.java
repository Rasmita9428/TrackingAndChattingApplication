package com.rasmitap.tailwebs_assigment2.model;

import java.util.ArrayList;
import java.util.List;

public class Datamodel {
    public String Marks;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String UserName;
    public Datamodel(String StudentName, String Marks, String Subject) {
        this.Marks=Marks;
        this.StudentName=StudentName;
        this.Subject=Subject;
    }

    public Datamodel() {

    }

    public String getMarks() {
        return Marks;
    }

    public void setMarks(String marks) {
        Marks = marks;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    private String StudentName;
    private String Subject;


    public static List<Datamodel> getList(){
        List<Datamodel> list=new ArrayList<>();
        list.add(new Datamodel("Voucher ","Biscuits","$8.00"));
        list.add(new Datamodel("Voucher ","Vegetables","$10.00"));
        list.add(new Datamodel("Voucher ","Soft Drinks","$15.00"));


        return list;
    }

}

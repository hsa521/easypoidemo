package com.javagi.model;

public class Student {
  /*  private String id;
    private String one;
    private String two;
    private String zq_xm;
    private String cw_xm;
    private String tj_xm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getZq_xm() {
        return zq_xm;
    }

    public void setZq_xm(String zq_xm) {
        this.zq_xm = zq_xm;
    }

    public String getCw_xm() {
        return cw_xm;
    }

    public void setCw_xm(String cw_xm) {
        this.cw_xm = cw_xm;
    }

    public String getTj_xm() {
        return tj_xm;
    }

    public void setTj_xm(String tj_xm) {
        this.tj_xm = tj_xm;
    }


    public Student(String id, String one, String two, String zq_xm, String cw_xm, String tj_xm) {
        this.id = id;
        this.one = one;
        this.two = two;
        this.zq_xm = zq_xm;
        this.cw_xm = cw_xm;
        this.tj_xm = tj_xm;
    }*/

    private int id;
    private String name;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

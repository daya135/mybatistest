package org.jzz.mybatistest.entity;

import org.apache.ibatis.type.Alias;

@Alias("AnnoAliasMyBean")    //还没看出作用
public class MyBean {
    int id;
    String name;

    @Override
    public String toString() {
        return "MyBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static MyBean getRandomBean() {
        MyBean bean = new MyBean();
        double s = Math.random() * 10;
        String name = "n" + String.valueOf(s).substring(0, 3);
        bean.setName(name);
        bean.setId((int)Math.round(s));
        return bean;
    }

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
}

package com.key.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by Administrator on 2017/5/6.
 */

@Entity
public class ClipBean {

    @Id(autoincrement = true)
    private Long id;

    private long c_time = System.currentTimeMillis();
    @Unique
    private String c_text;
    @Generated(hash = 153384378)
    public ClipBean(Long id, long c_time, String c_text) {
        this.id = id;
        this.c_time = c_time;
        this.c_text = c_text;
    }
    @Generated(hash = 106972086)
    public ClipBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public long getC_time() {
        return this.c_time;
    }
    public void setC_time(long c_time) {
        this.c_time = c_time;
    }
    public String getC_text() {
        return this.c_text;
    }
    public void setC_text(String c_text) {
        this.c_text = c_text;
    }





}

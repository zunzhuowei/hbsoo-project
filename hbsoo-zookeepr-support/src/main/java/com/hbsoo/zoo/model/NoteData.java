package com.hbsoo.zoo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by zun.wei on 2019/5/13 19:45.
 * Description:
 */
@Data
@Accessors(chain = true)
public class NoteData implements Serializable {

    private int version;

    private byte[] datas;

    private String data;


}

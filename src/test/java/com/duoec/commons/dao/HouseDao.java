package com.duoec.commons.dao;

import com.duoec.commons.pojo.house.House;
import org.springframework.stereotype.Service;

/**
 * Created by ycoe on 16/7/5.
 */
@Service
public class HouseDao extends BaseEntityDao<House> {
    @Override
    protected String getCollectionName() {
        return "house";
    }

    @Override
    public Class<House> getDocumentClass() {
        return House.class;
    }
}

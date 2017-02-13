package com.duoec.commons.dao;

import com.duoec.commons.pojo.process.CmsProcess;
import org.springframework.stereotype.Service;

/**
 * Created by ycoe on 17/2/10.
 */
@Service
public class CmsProcessDao extends BaseTestEntityDao<CmsProcess> {

    @Override
    protected String getCollectionName() {
        return "process";
    }

    @Override
    protected Class<CmsProcess> getDocumentClass() {
        return CmsProcess.class;
    }
}

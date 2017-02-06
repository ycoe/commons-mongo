package com.duoec.commons.mongo;

import org.bson.Document;

/**
 * Created by ycoe on 17/2/6.
 */
public class UpdateDoc {
    private Document setDocs;

    private Document unsetDocs;

    public Document getSetDocs() {
        return setDocs;
    }

    public void setSetDocs(Document setDocs) {
        this.setDocs = setDocs;
    }

    public Document getUnsetDocs() {
        return unsetDocs;
    }

    public void setUnsetDocs(Document unsetDocs) {
        this.unsetDocs = unsetDocs;
    }

    public void addSet(String key, Object value) {
        if (setDocs == null) {
            setDocs = new Document();
        }
        setDocs.put(key, value);
    }

    public void addUnset(String key, Object value) {
        if (unsetDocs == null) {
            unsetDocs = new Document();
        }
        unsetDocs.put(key, value);
    }

    public Document toDocument() {
        Document doc = new Document();
        if (setDocs != null) {
            if (setDocs.containsKey("id")) {
                setDocs.remove("id");
            }
            if (!setDocs.isEmpty()) {
                doc.put("$set", setDocs);
            }
        }
        if (unsetDocs != null) {
            if (unsetDocs.containsKey("id")) {
                unsetDocs.remove("id");
            }
            if (!unsetDocs.isEmpty()) {
                doc.put("$unset", unsetDocs);
            }
        }
        return doc;
    }
}

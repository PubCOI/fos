package org.pubcoi.fos.svc.models.dao;

import org.pubcoi.fos.svc.models.neo.nodes.ClientNode;

import java.util.HashSet;
import java.util.Set;

public class ClientNodeDAO {

    String id;
    String name;
    String postCode;
    Integer noticeCount;
    Set<NoticeNodeDAO> noticeNodeDAO = new HashSet<>();

    public ClientNodeDAO() {}

    public ClientNodeDAO(ClientNode client) {
        this.id = client.getId();
        this.name = client.getName();
        this.postCode = client.getPostCode();
        this.noticeCount = client.getNoticeRelationships().size();
    }

    public String getName() {
        return name;
    }

    public ClientNodeDAO setName(String name) {
        this.name = name;
        return this;
    }

    public String getId() {
        return id;
    }

    public ClientNodeDAO setId(String id) {
        this.id = id;
        return this;
    }

    public String getPostCode() {
        return postCode;
    }

    public ClientNodeDAO setPostCode(String postCode) {
        this.postCode = postCode;
        return this;
    }

    public Integer getNoticeCount() {
        return noticeCount;
    }

    public ClientNodeDAO setNoticeCount(Integer noticeCount) {
        this.noticeCount = noticeCount;
        return this;
    }

    public Set<NoticeNodeDAO> getNoticeNodeDAO() {
        return noticeNodeDAO;
    }

    public ClientNodeDAO setNoticeNodeDAO(Set<NoticeNodeDAO> noticeNodeDAO) {
        this.noticeNodeDAO = noticeNodeDAO;
        return this;
    }
}


package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Rabbitmq 参数类
 * @author chenxi
 * 创建时间：2019-7-26 17:57:50
 */
@DatabaseTable(tableName = "SyncParams")
public class SyncParams implements BaseModel {
	//id
    @DatabaseField(generatedId = true)
    private int id;
    //rabbitmq端口port
    @DatabaseField(columnName = "port")
    private String port;
    //rabbitmq机构唯一标示orgNo
    @DatabaseField(columnName = "orgNo")
    private String orgNo;
    //rabbitmq账号userName
    @DatabaseField(columnName = "userName")
    private String userName;
    //rabbitmq密码password
    @DatabaseField(columnName = "password")
    private String password;
    //rabbitmq服务器ip
    @DatabaseField(columnName = "ip")
    private String ip;
    //组织机构id（服务器端）
    @DatabaseField(columnName = "organizationId")
    private String organizationId;
    
    public SyncParams(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}

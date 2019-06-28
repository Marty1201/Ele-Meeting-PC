
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



/**
 * This is a JavaFx wrapper class for IdentityInfo, a JavaFx wrap class
 * encapsulates a Java primitive and adds some extra functionality 
 * (the classes under javafx.beans.property all contain built-in support 
 * for observability and binding as part of their design).
 * @author chenxi
 * 创建时间：2019-6-26 11:59:14
 */
public class IdentityInfoFx {
    //id
    private final IntegerProperty id = new SimpleIntegerProperty();
    //组织机构
    private final ObjectProperty<OrganInfoFx> organInfoFx = new SimpleObjectProperty<>();
    //身份中文名称
    private final StringProperty identityName = new SimpleStringProperty();
    //身份英文名称
    private final StringProperty identityEnglishName = new SimpleStringProperty();
    
    public IdentityInfoFx(){
        
    }
    
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }
    
    public OrganInfoFx getOrganInfoFx() {
        return organInfoFx.get();
    }

    public ObjectProperty<OrganInfoFx> organInfoFxProperty() {
        return organInfoFx;
    }

    public void setOrganInfoFx(OrganInfoFx organInfoFx) {
        this.organInfoFx.set(organInfoFx);
    }
    
    public String getIdentityName() {
        return identityName.get();
    }
    
    public StringProperty identityNameProperty(){
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName.set(identityName);
    }
    
    public String getIdentityEnglishName() {
        return identityEnglishName.get();
    }
    
    public StringProperty identityEnglishNameProperty(){
        return identityEnglishName;
    }

    public void setIdentityEnglishName(String identityEnglishName) {
        this.identityEnglishName.set(identityEnglishName);
    }
    
    @Override
    public String toString() {
        return "IdentityInfoFx{"
                + "id=" + id
                + ", organInfoFx" + organInfoFx
                + ", identityName=" + identityName
                + ", identityEnglishName" + identityEnglishName
                + '}';
    }
}

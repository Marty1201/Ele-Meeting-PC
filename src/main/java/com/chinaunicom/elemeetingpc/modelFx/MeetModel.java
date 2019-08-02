
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.MeetInfoDao;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zhaojunfeng
 */
public class MeetModel {
    
    //根据会议ID获取子会议
    public List<MeetInfo> queryChildMeetsByParentId(String meetParentId){
        List<MeetInfo> list = new ArrayList<>();
        MeetInfoDao dao = new MeetInfoDao();
        try {
            list = dao.findByFieldNameAndValue(MeetInfo.class, "parentMeetingId", meetParentId);
        } catch (ApplicationException ex) {
            Logger.getLogger(MeetModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
}


package com.chinaunicom.elemeetingpc.utils.exceptions;

/**
 * Customized application exception. 
 * 
 * @author chenxi
 * 创建时间：2019-6-19 9:57:07
 */
@SuppressWarnings("serial")
public class ApplicationException extends Exception{
    
    public ApplicationException(String message){
        super(message);
    }
}

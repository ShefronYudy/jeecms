/**
 * 
 */
package com.jeecms.main;

import com.jeecms.common.security.encoder.Md5PwdEncoder;

public class MainTest {

    private static void testMd5(){
        Md5PwdEncoder pwdEncoder = new Md5PwdEncoder();
        System.out.println(pwdEncoder.encodePassword("admin") );
    }
    
    public static void main(String[] args) {
        testMd5();

    }

}

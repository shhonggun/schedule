package com.dsmentoring.sync.main;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class StrMain {

    public static int SHA256_LENGTH=64;

    public static void main(String[] args){

        System.out.println(" 테스트  git commit3를 수행하고 있습니다. ");

        String str="cn=test4,ou=people,o=ssis";
        System.out.println("str = "+str);
        String[] ssss = str.split("=");
        String[] msg = ssss[1].split(",");
        //System.out.println(ssss[0]);
        //System.out.println(msg[0]);


        String passwd = "{SHA512}VKdIjZs3g8Z3qQU0Uc1zGCrM47HCJ9MC8KVlHj/OP4Tq9+LoYtcxO6+lij+jxl975tq+Xm4n7FWUmHBLihrLPw==";
        String passmsg = "";

        passwordMatches("Dsm1234!",passwd);

        System.out.println("test="+passmsg);
/*
        System.out.println("passwd = "+passwd);
        System.out.println("passwd = "+passmsg);

        byte[] message = "test".getBytes(StandardCharsets.UTF_8);
        String encode = Base64.getEncoder().encodeToString(message);

        System.out.println("encode = "+encode);


        byte[] decode = Base64.getDecoder().decode(passmsg);
        String dec = new String(decode, StandardCharsets.UTF_8);

        System.out.println("encode = "+dec.toLowerCase());*/


    }

    public static  String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x", b&0xff));
        return sb.toString();
    }

    public static boolean passwordMatches(String plainText ,String ldapPwd) {

        byte[] saltBytes;
        byte[] digestBytes = new byte[SHA256_LENGTH];
        int saltLength = 0;

        // LDAP 패스워드 Scheme 및 Base64 패스워드 분리
//		String ldapSchemeName = ldapPwd.substring(0, ldapPwd.indexOf('}')+1);
        String base64Pwd = ldapPwd.substring(ldapPwd.indexOf('}')+1);
        Base64.Decoder decoder = Base64.getDecoder();

        // Base64 패스워드 복호화
        try {

            //// 요기가 중요함. decodedBytes 타입을 String이 아닌 byte 배열로 사용해야함.
            byte[] decodedBytes = decoder.decode(base64Pwd);

                        // 제공된 LDAP 패스워드(digest + salt) 총 길이가 SHA256 digest 길이보다 작은 경우
            System.out.println(("length="+decodedBytes.length));

            System.arraycopy(decodedBytes, 0, digestBytes, 0, SHA256_LENGTH);

            System.out.println(byteArrayToHex(decodedBytes));

            String str = new String(digestBytes,"UTF-8");

            System.out.println("str");

        }
        catch (Exception e)	{
            System.out.println(e);
            return false;
        }


        int plainBytesLength = plainText.length();
        byte[] plainPlusSalt = new byte[plainBytesLength + saltLength];
        System.arraycopy(plainText.getBytes(), 0, plainPlusSalt, 0, plainBytesLength);

        // Salted SHA256 digest 생성
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        byte[] userDigestBytes = messageDigest.digest(plainPlusSalt);


        return true;

    }
}

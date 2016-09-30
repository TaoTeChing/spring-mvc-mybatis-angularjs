package com.crell.common.test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import java.security.Key;

/**
 * Created by crell on 2016/9/29.
 */
public class TestJwt {
    public static void main(String[] args) throws Exception {
        Key key = MacProvider.generateKey();
        System.out.println(key);

        String compactJws = Jwts.builder()
                .setSubject("cqi_825@sina.com")
                .signWith(SignatureAlgorithm.HS512, "cnjyz!QAZ_PL<")
                .compact();

        System.out.println(compactJws);

        String returnStr = Jwts.parser().setSigningKey("cnjyz!QAZ_PL<").parseClaimsJws(compactJws).getBody().getSubject();
        System.out.println(returnStr);
    }
}

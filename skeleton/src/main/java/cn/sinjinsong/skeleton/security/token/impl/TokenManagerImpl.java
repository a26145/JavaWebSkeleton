package cn.sinjinsong.skeleton.security.token.impl;

import cn.sinjinsong.common.cache.CacheManager;
import cn.sinjinsong.skeleton.exception.token.TokenStateInvalidException;
import cn.sinjinsong.skeleton.properties.AuthenticationProperties;
import cn.sinjinsong.skeleton.security.token.TokenManager;
import cn.sinjinsong.skeleton.security.token.TokenState;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

/**
 * Created by SinjinSong on 2017/4/27.
 */
@Component
public class TokenManagerImpl implements TokenManager {
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private AuthenticationProperties authenticationProperties;
    
    /**
     * 一个JWT实际上就是一个字符串，它由三部分组成，头部、载荷与签名。
     * iss: 该JWT的签发者，是否使用是可选的；
     * sub: 该JWT所面向的用户，是否使用是可选的；
     * aud: 接收该JWT的一方，是否使用是可选的；
     * exp(expires): 什么时候过期，这里是一个Unix时间戳，是否使用是可选的；
     * iat(issued at): 在什么时候签发的(UNIX时间)，是否使用是可选的；
     * 其他还有：
     * nbf (Not Before)：如果当前时间在nbf里的时间之前，则Token不被接受；一般都会留一些余地，比如几分钟；，是否使用是可选的；
     * 
     * JWT还需要一个头部，头部用于描述关于该JWT的最基本的信息，例如其类型以及签名所用的算法等。这也可以被表示成一个JSON对象。
     * {
     * "typ": "JWT",
     * "alg": "HS256"
     * }
     *
     * @param username 用户信息
     * @return
     */
    @Override
    public String createToken(String username) {
        //获取加密算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //生成签名密钥  
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(authenticationProperties.getSecretKey());
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        
        //添加构成JWT的参数  
        JwtBuilder builder = Jwts.builder().setHeaderParam("typ", "JWT")
                .setId(username)
                .setIssuedAt(now)
                .signWith(signatureAlgorithm, signingKey);

        //添加Token过期时间  
        long expireTime = System.currentTimeMillis() + authenticationProperties.getExpireTime()*1000;
        Date expireDateTime = new Date(expireTime);
        builder.setExpiration(expireDateTime);
        //生成JWT  
        String token = builder.compact();
        //放入缓存
        cacheManager.putWithExpireTime(token, username, authenticationProperties.getExpireTime());
        return token;
    }

    @Override
    public String checkToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(authenticationProperties.getSecretKey()))
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenStateInvalidException(TokenState.EXPIRE.toString());
        } catch(Exception e){
            throw new TokenStateInvalidException(TokenState.INVALID.toString());
        }
        
        String username = cacheManager.get(token, String.class);
        if(username == null){
            throw new TokenStateInvalidException(TokenState.INVALID.toString());
        }
        return username;
    }

    @Override
    public void deleteToken(String token) {
        cacheManager.delete(token);
    }
}

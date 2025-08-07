package me.fertiz.netflux.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil
{
    private static byte[] do_fuscate(byte[] secret, byte[] data)
    {
        for(int i=0; i<data.length; i++)
        {
            int _s = (secret[i % secret.length]+i) & 0xff;
            int _d = data[i] & 0xff;
            data[i] = (byte) (_s ^ _d);
        }
        return data;
    }

    public static byte[] obfuscate(byte[] secret, byte[] data)
    {
        return do_fuscate(secret, data);
    }

    
    public static byte[] deobfuscate(byte[] secret, byte[] data)
    {
        return do_fuscate(secret, data);
    }
    
    public static byte[] hashMac(String _name, byte[] _key, byte[]... _buffer)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        try {
            final SecretKeySpec _keySpec = new SecretKeySpec(_key, _name);
            final Mac _mac = Mac.getInstance(_name);
            _mac.init(_keySpec);
            for(byte[] _b : _buffer)
            {
                _mac.update(_b);
            }
            return _mac.doFinal();
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw e;
        }
    }
    
    public static byte[] hash(String _name, byte[]... _buffer)
            throws NoSuchAlgorithmException
    {
        try {
            MessageDigest _md = MessageDigest.getInstance(_name);
            for(byte[] _b : _buffer)
            {
                _md.update(_b);
            }
            return _md.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw e;
        }
    }
    
    public static byte[] sha512(byte[] _name)
            throws NoSuchAlgorithmException
    {
        return hash("SHA-512", _name);
    }
    
    public static byte[] sha512HMac(byte[] _key, byte[] _buffer)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        return hashMac("HMacSHA512", _key, _buffer);
    }
    
    public static byte[] makeKey(String _secret)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        return makeKey(_secret,"s3crEt$01!".getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] makeKey(String _secret, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeyException
    {
        return sha512HMac(_secret.getBytes(StandardCharsets.UTF_8), salt);
    }
}

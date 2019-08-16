package com.cn.leedane.security.sm4;
public class SM4_Context
{
    public int mode;

    public int[] sk;

    public boolean isPadding;

    public SM4_Context()
    {
        this.mode = 1;
        this.isPadding = true;
        this.sk = new int[32];
    }
}
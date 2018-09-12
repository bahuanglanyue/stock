package com.yingli.stock.server;

import com.sun.jna.ptr.ShortByReference;
import com.yingli.common.library.TdxLibrary;

public interface TdxHqLibrary extends TdxLibrary {
    void TdxHq_Disconnect();

    boolean TdxHq_Connect(String IP, int Port, byte[] Result, byte[] ErrInfo);

    boolean TdxHq_GetSecurityQuotes(byte[] Market, String[] Zqdm, ShortByReference Count, byte[] Result, byte[] ErrInfo);

    boolean TdxHq_GetXDXRInfo(byte Market, String Zqdm, byte[] Result, byte[] ErrInfo);

    boolean TdxHq_GetSecurityCount(byte Market, ShortByReference Result, byte[] ErrInfo);

    boolean TdxHq_GetSecurityList(byte Market, short Start, ShortByReference Count, byte[] Result, byte[] ErrInfo);
}
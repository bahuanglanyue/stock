package com.yingli.common.library;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ShortByReference;

public interface TdxLibrary extends Library {

    //打开通达信实例(客户端)
    public int OpenTdx(int pid, int port, byte[] ErrInfo);

    public int OpenTdx();

    //多账号-M版
    public void OpenTdx(int nClientType, String pszClientVersion, int nCliType, int nVipTermFlag, byte[] ErrInfo);

    //关闭通达信实例
    public void CloseTdx();

    public int IsConnectOK(int nClientID);

    /// <summary>
    /// 交易账户登录
    /// </summary>
    /// <param name="IP">券商交易服务器IP</param>
    /// <param name="Port">券商交易服务器端口</param>
    /// <param name="Version">设置通达信客户端的版本号:6.00或8.00</param>
    /// <param name="YybId">营业部编码：国泰君安为7</param>
    /// <param name="AccountNo">资金账号</param>
    /// <param name="TradeAccount">交易帐号与资金帐号相同</param>
    /// <param name="JyPassword">交易密码</param>
    /// <param name="TxPassword">通讯密码为空</param>
    /// <param name="ErrInfo">此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串</param>
    /// <returns>客户端ID，失败时返回-1。</returns>
    public int Logon(String IP, short Port, String Version, short YybID, String AccountNo, String TradeAccount, String JyPassword, String TxPassword, byte[] ErrInfo);

    //多账号-M版
    public int Logon(int qssid, String IP, short Port, String Version, short YybID, int nAccountType, String AccountNo, String TradeAccount, String JyPassword, String TxPassword, byte[] ErrInfo);

    /// <summary>
    /// 交易账户注销
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    public void Logoff(int ClientID);

    /// <summary>
    /// 查询各种交易数据
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="Category">表示查询信息的种类，0资金  1股份   2当日委托  3当日成交     4可撤单   5股东代码  6融资余额   7融券余额  8可融证券</param>
    /// <param name="Result">此API执行返回后，Result内保存了返回的查询数据, 形式为表格数据，行数据之间通过\n字符分割，列数据之间通过\t分隔。一般要分配1024*1024字节的空间。出错时为空字符串。</param>
    /// <param name="ErrInfo">此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串</param>
    public void QueryData(int ClientID, int Category, byte[] Result, byte[] ErrInfo);

    /// <summary>
    /// 下委托交易证券
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="Category">表示委托的种类，0买入 1卖出  2融资买入  3融券卖出   4买券还券   5卖券还款  6现券还券</param>
    /// <param name="PriceType">表示报价方式 0  上海限价委托 深圳限价委托 1深圳对方最优价格  2深圳本方最优价格  3深圳即时成交剩余撤销  4上海五档即成剩撤 深圳五档即成剩撤 5深圳全额成交或撤销 6上海五档即成转限价</param>
    /// <param name="Gddm">股东代码</param>
    /// <param name="Zqdm">证券代码</param>
    /// <param name="Price">委托价格</param>
    /// <param name="Quantity">委托数量</param>
    /// <param name="Result">同上</param>
    /// <param name="ErrInfo">同上</param>
    //融资买入 error 1:委托失败->(-61)[255151][资产账号控制表记录不存在] [p_fund_account=510100438,p_client_id=510100438]  =======>资金账号不对
    public void SendOrder(int ClientID, int Category, int PriceType, String Gddm, String Zqdm, float Price, int Quantity, byte[] Result, byte[] ErrInfo);

    /// <summary>
    /// 撤委托
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="ExchangeID">交易所类别， 上海1，深圳0(招商证券普通账户深圳是2)</param>
    /// <param name="hth">委托编号</param>
    /// <param name="Result">同上</param>
    /// <param name="ErrInfo">同上</param>
    public void CancelOrder(int ClientID, String ExchangeID, String hth, byte[] Result, byte[] ErrInfo);

    /// <summary>
    /// 获取证券的实时五档行情
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="Zqdm">证券代码</param>
    /// <param name="Result">同上</param>
    /// <param name="ErrInfo">同上</param>
    public void GetQuote(int ClientID, String Zqdm, byte[] Result, byte[] ErrInfo);

    /**
     * 参数配置有问题
     */
    /// <summary>
    /// 批量查询各种交易数据,用数组传入每个委托的参数，数组第i个元素表示第i个查询的相应参数。
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="Category">表示查询信息的种类，0资金  1股份   2当日委托  3当日成交     4可撤单   5股东代码  6融资余额   7融券余额  8可融证券</param>
    /// <param name="Count"></param>
    /// <param name="Result">同上</param>
    /// <param name="ErrInfo">同上</param>
    public  void QueryDatas(int ClientID, int[] Category, int Count, Pointer[] Result, Pointer[] ErrInfo);

    /// <summary>
    /// 批量下委托交易
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="Category">表示委托的种类，0买入 1卖出  2融资买入  3融券卖出   4买券还券   5卖券还款  6现券还券</param>
    /// <param name="PriceType">表示报价方式 0  上海限价委托 深圳限价委托 1深圳对方最优价格  2深圳本方最优价格  3深圳即时成交剩余撤销  4上海五档即成剩撤 深圳五档即成剩撤 5深圳全额成交或撤销 6上海五档即成转限价</param>
    /// <param name="Gddm">股东代码</param>
    /// <param name="Zqdm">证券代码</param>
    /// <param name="Price">委托价格</param>
    /// <param name="Quantity">委托数量</param>
    /// <param name="Count">批量下单数量</param>
    /// <param name="Result">同上</param>
    /// <param name="ErrInfo">同上</param>
    public  void SendOrders(int ClientID, int[] Category, int[] PriceType, String[] Gddm, String[] Zqdm, float[] Price, int[] Quantity, int Count, Pointer[] Result, Pointer[] ErrInfo);

    /// <summary>
    /// 批量撤单
    /// </summary>
    /// <param name="ClientID"></param>
    /// <param name="ExchangeID">交易所类别， 上海A1，深圳A0(招商证券普通账户深圳是2)</param>
    /// <param name="hth"></param>
    /// <param name="Count"></param>
    /// <param name="Result"></param>
    /// <param name="ErrInfo"></param>
    public  void CancelOrders(int ClientID, String[] ExchangeID, String[] hth, int Count, Pointer[] Result, Pointer[] ErrInfo);

    /// <summary>
    /// 批量获取证券的实时五档行情
    /// </summary>
    /// <param name="ClientID">客户端ID</param>
    /// <param name="Zqdm">证券代码</param>
    /// <param name="Count">证券合约数量</param>
    /// <param name="Result">同</param>
    /// <param name="ErrInfo">同</param>
    public  void GetQuotes(int ClientID, String[] Zqdm, int Count, Pointer[] Result, Pointer[] ErrInfo);

    /// <summary>
    ///  连接通达信行情服务器,服务器地址可在券商软件登录界面中的通讯设置中查得
    /// </summary>
    /// <param name="IP">服务器IP</param>
    /// <param name="Port">服务器端口</param>
    /// <param name="Result">此API执行返回后，Result内保存了返回的查询数据, 形式为表格数据，行数据之间通过\n字符分割，列数据之间通过\t分隔。一般要分配1024*1024字节的空间。出错时为空字符串。</param>
    /// <param name="ErrInfo">此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串。</param>
    /// <returns>成功返货true, 失败返回false</returns>
    public int TdxHq_Connect(String pszIP, short nPort, byte[] Result, byte[] ErrInfo);

    /// <summary>
    /// 断开同服务器的连接
    /// </summary>
    public void TdxHq_Disconnect();

    /*
    TdxHq_GetSecurityCount
    bool WINAPI TdxHq_GetSecurityCount( char nMarket, short *pnCount, char *pszErrInfo);
    功能： 获取指定市场内的证券数目
    参数： nMarket - 市场代码 0 深圳 1 上海 pnCount - 此 API 执行返回后，保存了返回的证券数量 pszErrInfo - 此 API 执行返回后，如果出错，保存了错误信息说明。一般要分配 256 字节的空间。没 出错时为空字符串。
    返回值: 成功返回 true, 失败返回 false
     */
    public  boolean TdxHq_GetSecurityCount(char nMarket, ShortByReference pnCount, byte[] pszErrInfo);

    /// <summary>
    /// 获取市场内从某个位置开始的1000支股票的股票代码
    /// </summary>
    /// <param name="Market">市场代码,   0->深圳     1->上海</param>
    /// <param name="Start">股票开始位置,第一个股票是0, 第二个是1, 依此类推,位置信息依据TdxL2Hq_GetSecurityCount返回的证券总数确定</param>
    /// <param name="Count">API执行后,保存了实际返回的股票数目,</param>
    /// <param name="Result">此API执行返回后，Result内保存了返回的证券代码信息,形式为表格数据，行数据之间通过\n字符分割，列数据之间通过\t分隔。一般要分配1024*1024字节的空间。出错时为空字符串。</param>
    /// <param name="ErrInfo">此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串。</param>
    /// <returns>成功返货true, 失败返回false</returns>
    public  boolean TdxHq_GetSecurityList(int nMarket, short nStart, ShortByReference pnCount, byte[] Result, byte[] ErrInfo);

    /// <summary>
    /// 获取除权除息信息
    /// </summary>
    /// <param name="Market">市场代码,   0->深圳     1->上海</param>
    /// <param name="Zqdm">证券代码</param>
    /// <param name="Result">此API执行返回后，Result内保存了返回的查询数据,出错时为空字符串。</param>
    /// <param name="ErrInfo">此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串。</param>
    /// <returns>成功返货true, 失败返回false</returns>
    public  boolean TdxHq_GetXDXRInfo(char nMarket, String pszZqdm, byte[] Result, byte[] ErrInfo);

    /// <summary>
    /// 获取五档报价
    /// </summary>
    /// <param name="Market">市场代码,   0->深圳     1->上海</param>
    /// <param name="Zqdm">证券代码</param>
    /// <param name="Count">API执行前,表示证券代码的记录数目, API执行后,保存了实际返回的记录数目</param>
    /// <param name="Result">此API执行返回后，Result内保存了返回的查询数据, 形式为表格数据，行数据之间通过\n字符分割，列数据之间通过\t分隔。一般要分配1024*1024字节的空间。出错时为空字符串。</param>
    /// <param name="ErrInfo">此API执行返回后，如果出错，保存了错误信息说明。一般要分配256字节的空间。没出错时为空字符串。</param>
    /// <returns>成功返货true, 失败返回false</returns>
    public  boolean TdxHq_GetSecurityQuotes(byte[] nMarket, String[] pszZqdm, ShortByReference pnCount, byte[] Result, byte[] ErrInfo);

    /**
     * 查询历史委托/成交/交割单
     *
     * @param ClientID
     * @param Category  查询信息的种类  : 0 历史委托 1 历史成交 2 交割单
     * @param StartDate 开始日期，格式为 yyyyMMdd,比如 2017 年 2 月 1 日为 20170201
     * @param EndDate   结束日期，格式为 yyyyMMdd,比如 2017 年 2 月 1 日为 20170201
     * @param Result
     * @param ErrInfo
     */
    public void QueryHistoryData(int ClientID, int Category, String StartDate, String EndDate, byte[] Result, byte[] ErrInfo);
}

